package com.lncanswer.rpc.bootstrap;

import com.lncanswer.rpc.RpcApplication;

/**
 * @author LNC
 * @version 1.0
 * @description 服务消费者启动类（初始化）
 * @date 2024/4/13 19:50
 */
public class ConsumerBootstrap {

    /**
     * 初始化
     */
    public static void init(){
        //RPC框架初始化（配置和注册中心）
        RpcApplication.init();
    }
}
