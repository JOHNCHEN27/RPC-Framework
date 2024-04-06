package com.lncanswer.rpc.config;

import lombok.Data;

/**
 * @author LNC
 * @version 1.0
 * @description RPC框架配置
 * @date 2024/4/6 9:12
 */
@Data
public class RpcConfig {

    /**
     * 名称
     */
    private String name = "lnc-rpc";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机号
     */
    private String serverHost = "localhost";

    /**
     * 服务器端口号
     */
    private Integer serverPort = 8080;
}
