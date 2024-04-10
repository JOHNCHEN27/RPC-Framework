package com.lncanswer.rpc.model;

import com.lncanswer.rpc.constant.RpcConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author LNC
 * @version 1.0
 * @description Rpc请求
 * @date 2024/4/2 22:52
 */
@Data
@Builder //创建实例对象时可以利用build链式调用方法为对象设置属性值
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务方法
     */
    private String serviceMethod;

    /**
     * 服务版本
     */
    private String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;

    /**
     * 参数类型列表
     */
    private Class<?> [] parameterTypes;

    /**
     * 参数列表
     */
    private Object[] args;
}
