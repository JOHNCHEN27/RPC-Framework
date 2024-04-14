package com.lncanswer.ex.consumer;

import com.lncanswer.ex.common.model.User;
import com.lncanswer.ex.common.service.UserService;
import com.lncanswer.rpc.bootstrap.ConsumerBootstrap;
import com.lncanswer.rpc.config.RpcConfig;
import com.lncanswer.rpc.proxy.ServiceProxyFactory;
import com.lncanswer.rpc.utils.ConfigUtils;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author LNC
 * @version 1.0
 * @description 简易消费者示例
 * @date 2024/4/6 10:35
 */
public class ConsumerExample {
    public static void main(String [] args){

        //服务提供者初始化
        ConsumerBootstrap.init();

        //获取代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("lnc");
        //调用
        User newUser = userService.getUser(user);
        if (newUser != null){
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }


//        //获取java配置对象，从配置文件读取 配置前缀为rpc对于的后缀属性
//        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class,"rpc");
//        System.out.println(rpc.toString());
//
//        Timer timer = new Timer();
//        TimerTask timerTask = new TimerTask(){
//
//            @Override
//            public void run() {
//                System.out.println(rpc.toString());
//            }
//        };
//        timer.schedule(timerTask,1000,3000);


//        UserService userService = ServiceProxyFactory.getMockProxy(UserService.class);
//        User user = new User();
//        user.setName("lnc");
//        //调用
//        User newUser = userService.getUser(user);
//        if (newUser != null){
//            System.out.println(newUser.getName());
//        } else {
//            System.out.println("user == null ");
//        }
//        String number = userService.getNumber();
//        System.out.println(number);

    }
}
