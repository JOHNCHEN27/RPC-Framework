package com.lncanswer.rpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.lncanswer.rpc.model.RpcRequest;
import com.lncanswer.rpc.model.RpcResponse;
import com.lncanswer.rpc.serializer.JdkSerializer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author LNC
 * @version 1.0
 * @description 动态代理（JDK动态代理）
 * @date 2024/4/3 0:17
 */
public class ServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     * @param proxy the proxy instance that the method was invoked on
     *
     * @param method the {@code Method} instance corresponding to
     * the interface method invoked on the proxy instance.  The declaring
     * class of the {@code Method} object will be the interface that
     * the method was declared in, which may be a superinterface of the
     * proxy interface that the proxy class inherits the method through.
     *
     * @param args an array of objects containing the values of the
     * arguments passed in the method invocation on the proxy instance,
     * or {@code null} if interface method takes no arguments.
     * Arguments of primitive types are wrapped in instances of the
     * appropriate primitive wrapper class, such as
     * {@code java.lang.Integer} or {@code java.lang.Boolean}.
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //指定序列化器
        JdkSerializer serializer = new JdkSerializer();

        //构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .serviceMethod(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        try{
            //序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            //发送请求
            //todo 这里地址暂且硬编码（实际需要使用注册中心和服务发现机制解决）
            try(HttpResponse httpResponse =
                        HttpRequest.post("http://localhost:8080").body(bodyBytes)
                                .execute()){
                byte[] result = httpResponse.bodyBytes();
                //反序列化
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
