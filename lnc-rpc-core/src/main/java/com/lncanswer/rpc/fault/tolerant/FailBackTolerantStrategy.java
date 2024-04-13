package com.lncanswer.rpc.fault.tolerant;

import com.lncanswer.rpc.model.RpcResponse;

import java.util.Map;

/**
 * @author LNC
 * @version 1.0
 * @description 降级到其他服务 -- 容错策略
 * @date 2024/4/13 14:09
 */
public class FailBackTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        //todo 后续扩展，获取降级的服务并调用
        return null;
    }
}
