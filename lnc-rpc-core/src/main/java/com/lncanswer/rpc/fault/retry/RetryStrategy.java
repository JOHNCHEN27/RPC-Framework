package com.lncanswer.rpc.fault.retry;


import com.lncanswer.rpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * @author LNC
 * @version 1.0
 * @description 重试策略
 * @date 2024/4/13 12:59
 */
public interface RetryStrategy {

    /**
     * 重试
     * @param callable 使用Callable类代表一个任务
     * @return
     * @throws Exception
     */
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
