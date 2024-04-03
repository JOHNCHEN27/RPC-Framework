package com.lncanswer.ex.provider;

import com.lncanswer.ex.common.service.UserService;
import com.lncanswer.rpc.registry.LocalRegistry;
import com.lncanswer.rpc.server.HttpServer;
import com.lncanswer.rpc.server.VertxHttpServer;

/**
 * @author LNC
 * @version 1.0
 * @description 简易服务提供者示例
 * @date 2024/4/2 15:27
 */
public class EasyProviderExample {
    public static void main(String [] args) {

        //注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        //启动web服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);
    }
}
