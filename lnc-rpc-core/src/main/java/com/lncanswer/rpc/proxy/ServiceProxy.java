package com.lncanswer.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.lncanswer.rpc.RpcApplication;
import com.lncanswer.rpc.config.RpcConfig;
import com.lncanswer.rpc.constant.RpcConstant;
import com.lncanswer.rpc.fault.retry.RetryStrategy;
import com.lncanswer.rpc.fault.retry.RetryStrategyFactory;
import com.lncanswer.rpc.fault.tolerant.TolerantStrategy;
import com.lncanswer.rpc.fault.tolerant.TolerantStrategyFactory;
import com.lncanswer.rpc.loadbalancer.LoadBalancer;
import com.lncanswer.rpc.loadbalancer.LoadBalancerFactory;
import com.lncanswer.rpc.model.RpcRequest;
import com.lncanswer.rpc.model.RpcResponse;
import com.lncanswer.rpc.model.ServiceMetaInfo;
import com.lncanswer.rpc.protocol.*;
import com.lncanswer.rpc.registry.Registry;
import com.lncanswer.rpc.registry.RegistryFactory;
import com.lncanswer.rpc.serializer.JdkSerializer;
import com.lncanswer.rpc.serializer.Serializer;
import com.lncanswer.rpc.serializer.SerializerFactory;
import com.lncanswer.rpc.server.tcp.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
//        //指定序列化器
//        JdkSerializer serializer = new JdkSerializer();

        //动态使用序列化器
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        //构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .serviceMethod(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();


            //序列化
            //byte[] bodyBytes = serializer.serialize(rpcRequest);
            //发送请求
            //从注册中心获取服务提供者请求地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)){
                throw new RuntimeException("暂无服务地址");
            }

           // ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);

            //负载均衡
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            //将调用方法名（请求路径）作为负载均衡参数
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("methodName",rpcRequest.getServiceMethod());
            ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);

//            //发送请求
//            try(HttpResponse httpResponse =
//                        HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
//                                .body(bodyBytes)
//                                .execute()){
//                byte[] result = httpResponse.bodyBytes();
//                //反序列化
//                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
//                return rpcResponse.getData();
//            }

            //发送TCP请求
//            Vertx vertx = Vertx.vertx();
//            NetClient netClient = vertx.createNetClient();
//            CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
//            netClient.connect(selectedServiceMetaInfo.getServicePort(),selectedServiceMetaInfo.getServiceHost(),
//                    result -> {
//                if (result.succeeded()){
//                    System.out.println("Connected to TCP server");
//                    NetSocket socket = result.result();
//                    //发送数据 构造消息
//                    ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
//                    ProtocolMessage.Header header = new ProtocolMessage.Header();
//                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
//                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
//                    header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(
//                            RpcApplication.getRpcConfig().getSerializer()).getKey());
//                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
//                    header.setRequestId(IdUtil.getSnowflakeNextId());
//                    protocolMessage.setHeader(header);
//                    protocolMessage.setBody(rpcRequest);
//                    //编码请求
//                    try {
//                        Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
//                        socket.write(encodeBuffer);
//                    } catch (Exception e){
//                        throw new RuntimeException("协议信息编码错误");
//                    }
//
//                    //接受响应
//                    socket.handler(buffer -> {
//                        try {
//                            ProtocolMessage<RpcResponse> rpcResponseProtocolMessage =
//                                    (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
//                            responseFuture.complete(rpcResponseProtocolMessage.getBody());
//                        } catch (IOException e){
//                            throw new RuntimeException("协议消息解码错误");
//                        }
//                    });
//                } else {
//                    System.err.println("Failed to connect to TCP server");
//                }
//                    });
//
//            RpcResponse rpcResponse = responseFuture.get();
//            //关闭连接
//            netClient.close();
//            return rpcResponse.getData();

            //发送TCP请求 使用重试机制
            RpcResponse rpcResponse;
            try {
                RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
                rpcResponse = retryStrategy.doRetry(() ->
                        VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo)
                );
            }catch (Exception e){
                //容错机制
                TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
                 rpcResponse = tolerantStrategy.doTolerant(null, e);
            }
            return rpcResponse.getData();


    }
}
