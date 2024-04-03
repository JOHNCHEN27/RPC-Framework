package com.lncanswer.ex.provider;

import com.lncanswer.ex.common.model.User;
import com.lncanswer.ex.common.service.UserService;

/**
 * @author LNC
 * @version 1.0
 * @description 用户服务实现类
 * @date 2024/4/2 15:23
 */
public class UserServiceImpl implements UserService{
    public User getUser(User user){
        System.out.println("用户名: " + user.getName());
        return user;
    }
}
