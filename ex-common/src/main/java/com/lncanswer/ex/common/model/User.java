package com.lncanswer.ex.common.model;

import java.io.Serializable;

/**
 * @author LNC
 * @version 1.0
 * @description 用户
 * @date 2024/4/2 15:08
 */
public class User  implements Serializable {
    //实现序列化接口 为后续网络传输序列化提供支持
    private String name;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

}
