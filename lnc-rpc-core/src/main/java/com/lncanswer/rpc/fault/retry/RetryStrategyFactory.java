package com.lncanswer.rpc.fault.retry;

import com.lncanswer.rpc.spi.SpiLoader;

/**
 * @author LNC
 * @version 1.0
 * @description 重试策略工厂(用于获取重试策略对象)
 * @date 2024/4/13 13:21
 */
public class RetryStrategyFactory {

    static {
        SpiLoader.load(RetryStrategy.class);
    }

    /**
     * 默认重试器
     */
    private static final RetryStrategy DEFAULT_RETRY_STRATEGY = new NoRetryStrategy();


    /**
     * 获取重试器实例对象
     * @param key
     * @return
     */
    public static RetryStrategy getInstance(String key){
        return SpiLoader.getInstance(RetryStrategy.class,key);
    }
}
