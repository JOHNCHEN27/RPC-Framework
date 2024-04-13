package com.lncanswer.rpc.fault.tolerant;

import com.lncanswer.rpc.model.RpcResponse;

import java.util.Map;

/**
 * @author LNC
 * @version 1.0
 * @description 转移到其他服务节点
 * @date 2024/4/13 14:11
 */
public class FailOverTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        //todo 后续扩展 获取其他服务节点并调用
        return null;
    }
}
