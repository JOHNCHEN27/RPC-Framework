package com.lncanswer.ex.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.lncanswer.ex.common.model.User;
import com.lncanswer.ex.common.service.UserService;
import com.lncanswer.rpc.model.RpcRequest;
import com.lncanswer.rpc.model.RpcResponse;
import com.lncanswer.rpc.serializer.JdkSerializer;

import java.io.IOException;

/**
 * @author LNC
 * @version 1.0
 * @description 静态代理
 * @date 2024/4/3 0:05
 */
public class UserServiceProxy implements UserService {
    @Override
    public User getUser(User user) {
        //指定序列化器对象
        JdkSerializer serializer = new JdkSerializer();

        //发送请求 链式调用方法生成对象
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .serviceMethod("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();

        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte [] result;
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080").body(bodyBytes)
                    .execute()){
                result = httpResponse.bodyBytes();
            }
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return (User) rpcResponse.getData();
        } catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }
}
