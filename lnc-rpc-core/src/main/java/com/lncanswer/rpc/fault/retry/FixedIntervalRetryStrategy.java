package com.lncanswer.rpc.fault.retry;

import com.github.rholder.retry.*;
import com.lncanswer.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author LNC
 * @version 1.0
 * @description 固定重试间隔策略实现
 * 使用Guava-Retrying提供的RetryBuilder能够很方便的指定重试条件、重试等待策略、重试停止策略、重试工作等
 * @date 2024/4/13 13:07
 */
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy{
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        Retryer<RpcResponse> retry = RetryerBuilder.<RpcResponse>newBuilder()
                //重试指定条件 到出现Exception异常时重试
                .retryIfExceptionOfType(Exception.class)
                //重试等待策略 选择fixedWait固定时间间隔策略
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS))
                //重试停止策略 选择stopAfterAttempt超过最大重试次数停止
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                //重试工作 监听重试，每次重试时，除了再次执行任务外，还能够打印当前的重试次数
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("重试次数： {}", attempt.getAttemptNumber());
                    }
                }).build();
        return retry.call(callable);
    }
}
