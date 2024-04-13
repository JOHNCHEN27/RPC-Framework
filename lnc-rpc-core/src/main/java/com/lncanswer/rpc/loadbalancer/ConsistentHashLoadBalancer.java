package com.lncanswer.rpc.loadbalancer;

import com.lncanswer.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author LNC
 * @version 1.0
 * @description 一致性 Hash负载均衡器
 * 使用 TreeMap实现一致性 Hash环，该数据结构提供了ceilingEntry和firstEntry两个方法，便于获取符合算法要求的节点
 * @date 2024/4/12 21:32
 */
public class ConsistentHashLoadBalancer implements LoadBalancer{

    /**
     * 一致性Hash环，存放虚拟节点
     */
    private final TreeMap<Integer,ServiceMetaInfo> virtualNodes = new TreeMap<>();

    /**
     * 虚拟节点数
     */
    private static final int VIRTUAL_NODE_NUM = 100;
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()){
            return null;
        }

        //构建虚拟节点环
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList){
            for (int i = 0; i <VIRTUAL_NODE_NUM ; i++) {
                int hash = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
                virtualNodes.put(hash,serviceMetaInfo);
            }
        }
        //获取调用请求的hash值
        int hash = getHash(requestParams);
        //选择最接近且大于等于调用请求hash值的虚拟节点
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if (entry == null){
            //如果没有大于等于调用请求hash值的虚拟节点，则返回环首部节点
            entry = virtualNodes.firstEntry();
        }
        return entry.getValue();
    }

    /**
     * Hash算法，可自行实现
     * @param key
     * @return
     */
    private int getHash(Object key){
        return key.hashCode();
    }
}
