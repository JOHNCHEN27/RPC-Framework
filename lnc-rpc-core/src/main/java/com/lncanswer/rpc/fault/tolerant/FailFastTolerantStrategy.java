package com.lncanswer.rpc.fault.tolerant;

import com.lncanswer.rpc.model.RpcResponse;

import java.util.Map;

/**
 * @author LNC
 * @version 1.0
 * @description 快速失败容错 -- 遇到异常后，将异常抛出给外层处理
 * @date 2024/4/13 14:04
 */
public class FailFastTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        throw  new RuntimeException("服务报错",e);
    }
}
