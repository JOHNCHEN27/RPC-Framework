package com.lncanswer.rpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LNC
 * @version 1.0
 * @description 本地服务注册器
 * @date 2024/4/2 16:25
 */
public class LocalRegistry {

    /**
     * 注册信息存储
     * 使用ConcurrentHashMap 线程安全
     */
    private static final Map<String,Class<?>> map =  new ConcurrentHashMap<>();

    /**
     * 注册服务
     * @param serviceName
     * @param implClass
     */
    public static void register(String serviceName,Class<?> implClass){
        map.put(serviceName,implClass);
    }

    /**
     * 获取服务
     * @param serviceName
     * @return
     */
    public static Class<?> get(String serviceName){
        return map.get(serviceName);
    }

    /**
     * 删除服务
     * @param serviceName
     */
    public static void remove(String serviceName){
        map.remove(serviceName);
    }
}
