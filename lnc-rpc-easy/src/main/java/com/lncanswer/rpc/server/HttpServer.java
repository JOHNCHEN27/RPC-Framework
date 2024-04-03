package com.lncanswer.rpc.server;

/**
 * @author LNC
 * @version 1.0
 * @description Http 服务器接口
 * @date 2024/4/2 15:52
 */
public interface HttpServer {
    /**
     * 启动服务器
     * @param port
     */
    void doStart(int port);
}
