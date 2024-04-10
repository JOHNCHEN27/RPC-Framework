package com.lncanswer.rpc.config;

import lombok.Data;

/**
 * @author LNC
 * @version 1.0
 * @description Rpc框架注册中心配置
 * @date 2024/4/9 10:49
 */
@Data
public class RegistryConfig {

    /**
     * 注册中心类别
     */
    private String registry = "etcd";

    /**
     * 注册中心地址
     */
    private String address = "http://localhost:2380";

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 超时时间(单位毫秒)
     */
    private Long timeout = 10000L;
}

