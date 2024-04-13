package com.lncanswer.rpc.fault.tolerant;

import com.lncanswer.rpc.model.RpcResponse;

import java.util.Map;

/**
 * @author LNC
 * @version 1.0
 * @description 容错策略
 * @date 2024/4/13 14:02
 */
public interface TolerantStrategy {

    /**
     * 容错
     * @param context 上下文 用于传递数据
     * @param e 异常
     * @return 由于容错是应用发生请求操作的，所以方法的返回值对象是响应对象
     */
    RpcResponse doTolerant(Map<String,Object> context,Exception e);
}
