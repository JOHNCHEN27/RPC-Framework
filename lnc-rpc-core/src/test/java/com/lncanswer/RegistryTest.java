package com.lncanswer;

import com.lncanswer.rpc.config.RegistryConfig;
import com.lncanswer.rpc.model.ServiceMetaInfo;
import com.lncanswer.rpc.registry.EtcdRegistry;
import com.lncanswer.rpc.registry.Registry;
import org.junit.Before;
import org.junit.Test;

/**
 * @author LNC
 * @version 1.0
 * @description 注册中心测试
 * @date 2024/4/10 19:46
 */
public class RegistryTest {

    final Registry registry = new EtcdRegistry();

    /**
     * Before注解： 所有Test测试方法执行之前，会先执行Before注解对应的方法
     */
    @Before
    public void init(){
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("http://localhost:2379");
        registry.init(registryConfig);
    }

    @Test
    public void heartBeat() throws Exception{
        register();
        Thread.sleep(60 * 1000L);
    }

    @Test
    public void register() throws Exception{
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        registry.register(serviceMetaInfo);
        serviceMetaInfo =new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1235);
        registry.register(serviceMetaInfo);
        serviceMetaInfo =new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("2.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        registry.register(serviceMetaInfo);
    }
}
