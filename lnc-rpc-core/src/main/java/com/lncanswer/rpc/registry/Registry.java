package com.lncanswer.rpc.registry;

import com.lncanswer.rpc.config.RegistryConfig;
import com.lncanswer.rpc.model.ServiceMetaInfo;

import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @description 注册中心接口
 * 遵循可扩展设计，后续可以实现多种不同注册中心，和序列化器一样，使用SPI机制动态加载
 * @date 2024/4/9 10:54
 */
public interface Registry {

    /**
     * 初始化
     * @param registryConfig
     */
    void init(RegistryConfig registryConfig);

    /**
     * 注册服务（服务端）
     * @param serviceMetaInfo
     * @throws Exception
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 注销服务（服务端）
     * @param serviceMetaInfo
     */
    void unRegister(ServiceMetaInfo serviceMetaInfo);


    /**
     * 服务发现（获取某服务的所有节点，消费端）
     * @param serviceKey
     * @return
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    /**
     * 服务销毁
     */
    void destroy();

    /**
     * 心跳检测(服务器)
     */
    void heartBeat();

    /**
     * 监听（消费端）
     * @param serviceNodeKey
     */
    void watch(String serviceNodeKey);
}
