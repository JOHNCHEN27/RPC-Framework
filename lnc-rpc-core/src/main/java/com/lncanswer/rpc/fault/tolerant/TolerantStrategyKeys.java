package com.lncanswer.rpc.fault.tolerant;

/**
 * @author LNC
 * @version 1.0
 * @description 容错策略键名常量
 * @date 2024/4/13 14:12
 */
public interface TolerantStrategyKeys {

    /**
     * 故障恢复
     */
    String FAIL_BACK = "failBack";

    /**
     * 快速失败
     */
    String FAIL_FAST = "failFast";

    /**
     * 故障转移
     */
    String FAIL_OVER = "failOver";

    /**
     * 静默处理
     */
    String FAIL_SAFE = "failSafe";
}
