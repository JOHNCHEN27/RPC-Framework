package com.lncanswer.ex.consumer;

import com.lncanswer.rpc.config.RpcConfig;
import com.lncanswer.rpc.utils.ConfigUtils;

/**
 * @author LNC
 * @version 1.0
 * @description 简易消费者示例
 * @date 2024/4/6 10:35
 */
public class ConsumerExample {
    public static void main(String [] args){
        //获取java配置对象，从配置文件读取 配置前缀为rpc对于的后缀属性
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class,"rpc");
        System.out.println(rpc.toString());

    }
}
