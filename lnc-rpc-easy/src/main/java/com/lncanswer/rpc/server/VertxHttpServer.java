package com.lncanswer.rpc.server;

import io.vertx.core.Vertx;

/**
 * @author LNC
 * @version 1.0
 * @description 监听指定端口并处理请求
 * @date 2024/4/2 15:54
 */
public class VertxHttpServer implements HttpServer{

    /**
     * 启动服务器
     * @param port
     */
    @Override
    public void doStart(int port) {
        //创建 Vert.x实例
        Vertx vertx = Vertx.vertx();

        //创建Http服务器
        io.vertx.core.http.HttpServer httpServer = vertx.createHttpServer();

        //监听端口并处理请求
//        httpServer.requestHandler(request -> {
//            //处理Http请求
//            System.out.println("Received request: " + request.method() + " " + request.uri());
//
//            //发生Http响应
//            request.response()
//                    .putHeader("content-type","text/plain")
//                    .end("Hello from Vertx HTTP server!");
//        });

        //绑定请求处理器
        httpServer.requestHandler(new HttpServerHandler());

        //启动Http服务器并监听指定端口
        httpServer.listen(port,result-> {
            if (result.succeeded()){
                System.out.println("监听的端口号为： " + port);
            } else {
                System.out.println("失败启动服务: " + result.cause() );
            }
        });
    }
}
