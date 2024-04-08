package com.lncanswer.rpc.server;

import com.lncanswer.rpc.RpcApplication;
import com.lncanswer.rpc.model.RpcRequest;
import com.lncanswer.rpc.model.RpcResponse;
import com.lncanswer.rpc.registry.LocalRegistry;
import com.lncanswer.rpc.serializer.Serializer;
import com.lncanswer.rpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author LNC
 * @version 1.0
 * @description HTTP请求处理
 * @date 2024/4/2 23:06
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
        //指定序列化器
//        final Serializer serializer = new JdkSerializer();

        //动态创建序列化器
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        //记录日志
        System.out.println("Received request: " + request.method() + " " + request.uri());

        //异步处理Http请求
        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try{
                rpcRequest = serializer.deserialize(bytes,RpcRequest.class);
            } catch (Exception e){
                e.printStackTrace();
            }

            //构造响应结果对象
            RpcResponse rpcResponse = new RpcResponse();
            //如果请求为null 直接返回
            if (rpcRequest == null) {
                rpcResponse.setMessage("rpcMessage is null ");
                doResponse(request,rpcResponse,serializer);
                return;
            }

            try {
                //获取要调用的服务实现类，通过反射调用
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getServiceMethod(),rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                //封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e){
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            //响应
            doResponse(request,rpcResponse,serializer);
        });

    }

    /**
     * 响应信息
     * @param request
     * @param rpcResponse
     * @param serializer
     */
    private void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse response = request.response().putHeader("content-type","application/json");
        try{
            //序列化
            byte[] serialized = serializer.serialize(rpcResponse);
            response.end(Buffer.buffer(serialized));
        }catch (IOException e){
            e.printStackTrace();
            response.end(Buffer.buffer());
        }
    }


}
