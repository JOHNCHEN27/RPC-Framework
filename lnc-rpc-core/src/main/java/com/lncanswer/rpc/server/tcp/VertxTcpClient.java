package com.lncanswer.rpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.lncanswer.rpc.RpcApplication;
import com.lncanswer.rpc.model.RpcRequest;
import com.lncanswer.rpc.model.RpcResponse;
import com.lncanswer.rpc.model.ServiceMetaInfo;
import com.lncanswer.rpc.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author LNC
 * @version 1.0
 * @description TCP客户端实现
 * @date 2024/4/11 17:05
 */
public class VertxTcpClient {

    /**
     * 发送请求
     * @param rpcRequest
     * @param serviceMetaInfo
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo)
        throws InterruptedException, ExecutionException{
        //发送TCP请求
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(),
                result -> {
            if (!result.succeeded()){
                System.err.println("Failed to connect to TCP server");
                return;
            }
                    NetSocket socket = result.result();
            //发送消息 构造消息
                    ProtocolMessage<Object> protocolMessage = new ProtocolMessage<>();
                    ProtocolMessage.Header header = new ProtocolMessage.Header();
                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                    header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(
                            RpcApplication.getRpcConfig().getSerializer()).getKey());
                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                    //生成全局请求id
                    header.setRequestId(IdUtil.getSnowflakeNextId());
                    protocolMessage.setHeader(header);
                    protocolMessage.setBody(rpcRequest);

                    //编码请求
                    try {
                        Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                        socket.write(encodeBuffer);
                    } catch (Exception e){
                        throw new RuntimeException("协议消息编码错误");
                    }

                    //接受响应
                    TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(
                            buffer -> {
                                try {
                                    ProtocolMessage<RpcResponse> responseProtocolMessage = (ProtocolMessage <RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                                    responseFuture.complete(responseProtocolMessage.getBody());
                                } catch (IOException e){
                                    throw new RuntimeException("协议消息解码错误");
                                }
                            }
                    );
                    socket.handler(bufferHandlerWrapper);
                });
        RpcResponse rpcResponse = responseFuture.get();
        //关闭连接
        netClient.close();
        return rpcResponse;
    }


//    public void start(){
//        //创建Vert.x实例
//        Vertx vertx = Vertx.vertx();
//
//        vertx.createNetClient().connect(8888,"localhost",result -> {
//            if (result.succeeded()){
//                System.out.println("Connected to TCP server");
//                io.vertx.core.net.NetSocket socket = result.result();
//                //发送数据
//                socket.write("Hello, server !");
//                //接受响应
//                socket.handler(buffer -> {
//                    System.out.println("Received response from server: " + buffer.toString());
//                });
//            } else {
//                System.out.println("Failed to connect to TCP server");
//            }
//        });
//    }

    public static void main(String[] args) {

    }
}
