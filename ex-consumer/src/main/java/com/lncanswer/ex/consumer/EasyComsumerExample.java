package com.lncanswer.ex.consumer;

import com.lncanswer.ex.common.model.User;
import com.lncanswer.ex.common.service.UserService;
import com.lncanswer.rpc.proxy.ServiceProxyFactory;

/**
 * @author LNC
 * @version 1.0
 * @description 建议消费者示例
 * @date 2024/4/2 15:41
 */
public class EasyComsumerExample {
    public static void main(String[] args) {
        // todo需要获取UserService的实现类对象
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("lnc");
        //调用
        User newUser = userService.getUser(user);
        if (newUser != null){
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null ");
        }
    }
}
