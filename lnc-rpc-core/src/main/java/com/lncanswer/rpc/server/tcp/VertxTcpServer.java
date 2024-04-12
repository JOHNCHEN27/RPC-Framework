package com.lncanswer.rpc.server.tcp;

import com.lncanswer.rpc.server.HttpServer;
import com.lncanswer.rpc.server.VertxHttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;

/**
 * @author LNC
 * @version 1.0
 * @description Tcp服务器实现(VertX)
 * @date 2024/4/11 16:53
 */
public class VertxTcpServer implements HttpServer {

    private byte[] handleRequest(byte[] requestData){
        //在这里编写处理请求的逻辑，根据requestData 构造响应数据并返回
        //先示例返回假数据 后续改动
        return "Hello,client !".getBytes();
    }
    @Override
    public void doStart(int port) {
        //创建Vert.x示例
        Vertx vertx = Vertx.vertx();

        //创建TCP服务器
        NetServer server = vertx.createNetServer();

        //处理请求
//        server.connectHandler(socker ->{
//            //处理连接
//            socker.handler(buffer -> {
//                //处理接受到的字节数组
//                byte[] requestData = buffer.getBytes();
//                //在这里进行自定义的字节数组处理逻辑，比如解析请求、调用服务、构造响应等
//                byte[] responseData = handleRequest(requestData);
//                //发送响应 向连接到服务器的客户端发送数据，数据格式为Buffer 这是Vert.x为我们提供的字节数组缓冲区实现
//                socker.write(Buffer.buffer(responseData));
//            });
//        });

        //处理请求
        server.connectHandler(new TcpServerHandler());

        //启动TCP服务器并监听指定端口号
        server.listen(port,result ->{
            if (result.succeeded()){
                System.out.println("TCP server started on port " + port);
            } else {
                System.err.println("Failed to start TCP server: " + result.cause());
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}
