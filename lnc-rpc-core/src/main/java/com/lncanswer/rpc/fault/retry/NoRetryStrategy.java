package com.lncanswer.rpc.fault.retry;

import com.lncanswer.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * @author LNC
 * @version 1.0
 * @description 不重试策略实现 --直接执行一次任务
 * @date 2024/4/13 13:05
 */
@Slf4j
public class NoRetryStrategy implements RetryStrategy{
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}
