package com.lncanswer.rpc.registry;

import com.lncanswer.rpc.model.ServiceMetaInfo;

import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @description 注册中心服务本地缓存
 * @date 2024/4/10 21:14
 */
public class RegistryServiceCache {

    /**
     * 服务缓存
     */
    List<ServiceMetaInfo> serviceCache;

    /**
     * 写缓存
     * @param newServiceCache
     */
    void writeCache(List<ServiceMetaInfo> newServiceCache){
        this.serviceCache = newServiceCache;
    }

    /**
     *读缓存
     * @return
     */
    List<ServiceMetaInfo> readCache(){
        return this.serviceCache;
    }

    /**
     * 清空缓存
     */
    void clearCache(){
        this.serviceCache = null;
    }
}
