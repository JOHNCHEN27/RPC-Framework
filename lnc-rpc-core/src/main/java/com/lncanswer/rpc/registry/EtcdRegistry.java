package com.lncanswer.rpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;

import cn.hutool.json.JSONUtil;
import com.lncanswer.rpc.config.RegistryConfig;
import com.lncanswer.rpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author LNC
 * @version 1.0
 * @description
 * @date 2024/4/9 11:01
 */
@Slf4j
public class EtcdRegistry implements Registry{
    private Client client;
    private KV kvClient;

    /**
     * 本机注册的节点key集合（用于key续期）
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    /**
     * 根节点
     */
    private static final String ETCD_ROOT_PATH = "rpc";

    /**
     * 正在监听的key集合 -- 使用ConcurrentHashSet防止并发冲突
     * 使用监听key集合的目的是为了防止监听的key重复
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    /**
     * 注册中心服务缓存
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder().endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).build();
        kvClient = client.getKVClient();
        //开启注册中心心跳检测和续期
        heartBeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        //创建Lease和KV客户端
        Lease leaseClient = client.getLeaseClient();

        //创建一个30秒的租约
        long leaseId = leaseClient.grant(30).get().getID();

        //设置要存储的键值对
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo),StandardCharsets.UTF_8);

        //将键值对和租约关联起来，并设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key,value,putOption).get();

        //添加节点信息到本地缓存
        localRegisterNodeKeySet.add(registerKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(registerKey,StandardCharsets.UTF_8));
        //从本地缓存移除续期key
        localRegisterNodeKeySet.remove(registerKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        //优先从缓存中获取服务信息
        List<ServiceMetaInfo> cacheServiceMetaInfoList = registryServiceCache.readCache();
        if (cacheServiceMetaInfoList != null && cacheServiceMetaInfoList.size()!=0){
            log.info("从缓存中读取数据:{}",cacheServiceMetaInfoList);
            return cacheServiceMetaInfoList;
        }
        //前缀搜索，结尾一定要加  " / "
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";

        try {
            // 前缀查询
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8), getOption)
                    .get().getKvs();
            //解析服务信息
            List<ServiceMetaInfo> serviceMetaInfoList = keyValues.stream().map(keyValue -> {
                String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                //监听key的变化
                watch(key);
                String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                return JSONUtil.toBean(value,ServiceMetaInfo.class);
            }).collect(Collectors.toList());

            //写入服务缓存
            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败",e);
        }
    }

    @Override
    public void destroy() {
        System.out.println("当前节点下线");
        //销毁时将本地存储的节点key集合取出在Etcd中删除对应内容
        //遍历所有节点的key进行删除
        for (String key : localRegisterNodeKeySet) {
            try {
                kvClient.delete(ByteSequence.from(key,StandardCharsets.UTF_8)).get();
            } catch (Exception e){
                throw new RuntimeException(key + "节点下线失败");
            }
        }
        //释放资源
        if (kvClient != null){
            kvClient.close();
        }
        if (client != null){
            client.close();
        }
    }

    /**
     * 心跳检测 --定时检测服务是否宕机
     * 使用Hutool工具类中的CronUtil实现定时任务，对所有集合中的节点执行重新注册操作
     * 是一个小trick 相当于续签
     */
    @Override
    public void heartBeat() {
        //10秒续签一次 续期时间要小于过期时间（设置的是30秒过期）允许容错一次
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                //遍历本节点所有的key
                for (String key : localRegisterNodeKeySet) {
                  try {
                      List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                              .get()
                              .getKvs();
                      //该节点已过期（需要重启节点才能重新注册）
                      if (CollUtil.isEmpty(keyValues)){
                          continue;
                      }

                      //节点未过期，重新注册（续签）
                      KeyValue keyValue = keyValues.get(0);
                      String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                      ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                      register(serviceMetaInfo);

                  }catch (Exception e){
                      throw new RuntimeException(key + "续签失败",e);
                  }

                }
            }
        });
        //支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    /**
     * 监听（消费端）
     * @param serviceNodeKey
     */
    @Override
    public void watch(String serviceNodeKey) {
        Watch watchClient = client.getWatchClient();
        //开启监听
        boolean newWatch = watchingKeySet.add(serviceNodeKey);
        log.info("添加服务key到缓存：{}",serviceNodeKey);
        if (newWatch){
            watchClient.watch(ByteSequence.from(serviceNodeKey,StandardCharsets.UTF_8), response -> {
                for (WatchEvent event : response.getEvents()){
                    switch (event.getEventType()){
                        // key删除时触发
                        case DELETE:
                            //清理注册服务缓存
                            registryServiceCache.clearCache();
                            break;
                        case PUT:
                        default:
                            break;
                    }
                }
                    });
        }

    }
}
