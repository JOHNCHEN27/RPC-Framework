package com.lncanswer.rpc.bootstrap;

import com.lncanswer.rpc.RpcApplication;
import com.lncanswer.rpc.config.RpcConfig;
import com.lncanswer.rpc.model.ServiceMetaInfo;
import com.lncanswer.rpc.model.ServiceRegisterInfo;
import com.lncanswer.rpc.registry.LocalRegistry;
import com.lncanswer.rpc.registry.Registry;
import com.lncanswer.rpc.registry.RegistryFactory;
import com.lncanswer.rpc.server.tcp.VertxTcpServer;

import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @description 服务提供者初始化
 * @date 2024/4/13 19:36
 */
public class ProviderBootstrap {

    /**
     * 初始化
     * @param
     */
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        //Rpc框架初始化
        RpcApplication.init();
        //全局配置
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        //注册服务
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName();
            //本地注册
            LocalRegistry.register(serviceName,serviceRegisterInfo.getImplClass());

            //注册服务到注册中心
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch ( Exception e){
                throw new RuntimeException(serviceName + "服务注册失败");
            }
        }


        //启动服务器
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(rpcConfig.getServerPort());
    }
}
