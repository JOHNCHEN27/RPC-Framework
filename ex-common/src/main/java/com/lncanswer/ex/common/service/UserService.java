package com.lncanswer.ex.common.service;

import com.lncanswer.ex.common.model.User;

/**
 * @author LNC
 * @version 1.0
 * @description 用户服务
 * @date 2024/4/2 15:08
 */
public interface UserService {

    /**
     * 获取用户
     * @param user
     * @return
     */
    User getUser(User user);

    /**
     * 新方法 获取数字
     * @return
     */
    default String getNumber(){
        return "haha";
    }
}
