package com.lncanswer.rpc.fault.retry;

/**
 * @author LNC
 * @version 1.0
 * @description 重试策略常量键名
 * @date 2024/4/13 13:19
 */
public interface RetryStrategyKeys {

    /**
     * 不重试
     */
    String NO ="no";

    /**
     * 固定时间间隔
     */
    String FIXED_INTERVAL = "fixedInterval";
}
