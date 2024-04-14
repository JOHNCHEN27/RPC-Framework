package com.lncanswer.ex.provider;

import com.lncanswer.ex.common.model.User;
import com.lncanswer.ex.common.service.UserService;
import com.lncanswer.rpc.RpcApplication;
import com.lncanswer.rpc.bootstrap.ProviderBootstrap;
import com.lncanswer.rpc.config.RegistryConfig;
import com.lncanswer.rpc.config.RpcConfig;
import com.lncanswer.rpc.model.ServiceMetaInfo;
import com.lncanswer.rpc.model.ServiceRegisterInfo;
import com.lncanswer.rpc.registry.LocalRegistry;
import com.lncanswer.rpc.registry.Registry;
import com.lncanswer.rpc.registry.RegistryFactory;
import com.lncanswer.rpc.server.VertxHttpServer;
import com.lncanswer.rpc.server.tcp.VertxTcpServer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @description 服务提供者示例
 * @date 2024/4/9 20:49
 */
public class ProviderExample {
    public static void main(String[] args) {

        //要注册的服务
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo<UserService> serviceRegisterInfo =
                new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);

        //服务提供者初始化
        ProviderBootstrap.init(serviceRegisterInfoList);

//        //Rpc框架初始化
//        RpcApplication.init();
//
//        //注册服务
//        String serviceName = UserService.class.getName();
//        LocalRegistry.register(serviceName, UserServiceImpl.class);
//
//        //注册服务到注册中心
//        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
//        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
//        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
//        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
//        serviceMetaInfo.setServiceName(serviceName);
//       // serviceMetaInfo.setServiceAddress(rpcConfig.getServerHost() + ":" +rpcConfig.getServerPort());
//        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
//        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
//
//        try {
//            registry.register(serviceMetaInfo);
//        }catch (Exception e){
//            throw new RuntimeException(e);
//        }
//
//        //启动Web服务
//       // VertxHttpServer httpServer = new VertxHttpServer();
//       // httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
//
//        //启动TCP服务
//        VertxTcpServer vertxTcpServer = new VertxTcpServer();
//        vertxTcpServer.doStart(8080);
      }
}
