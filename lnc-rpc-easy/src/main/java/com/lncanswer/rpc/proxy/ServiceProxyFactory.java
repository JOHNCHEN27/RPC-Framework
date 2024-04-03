package com.lncanswer.rpc.proxy;

import java.lang.reflect.Proxy;

/**
 * @author LNC
 * @version 1.0
 * @description 服务代理工厂（用于创建代理对象，使用工厂设计模式）
 * @date 2024/4/3 0:26
 */
public class ServiceProxyFactory {

    /**
     * 根据服务类创建代理对象
     * @param serviceClass
     * @return
     * @param <T>
     */
    public static <T> T getProxy(Class<T> serviceClass){
        //newProxyInstance方法指定类型创建代理对象
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy());
    }
}
