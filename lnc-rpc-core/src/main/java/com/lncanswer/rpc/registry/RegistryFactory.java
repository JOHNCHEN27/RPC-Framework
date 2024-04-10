package com.lncanswer.rpc.registry;

import com.lncanswer.rpc.spi.SpiLoader;

/**
 * @author LNC
 * @version 1.0
 * @description 注册中心工厂（用于获取注册中心对象）
 * @date 2024/4/9 16:02
 */
public class RegistryFactory {

    //静态代码块 随着类加载而加载
    static {
        SpiLoader.load(Registry.class);
    }

    //默认注册中心
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    /**
     * 获取实例
     * @param key
     * @return
     */
    public static Registry getInstance(String key){
        return SpiLoader.getInstance(Registry.class,key);
    }
}
