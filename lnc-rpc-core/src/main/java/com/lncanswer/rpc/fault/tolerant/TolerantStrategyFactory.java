package com.lncanswer.rpc.fault.tolerant;

import com.lncanswer.rpc.spi.SpiLoader;

/**
 * @author LNC
 * @version 1.0
 * @description 容错策略工厂（用于获取容错对象实例）
 * @date 2024/4/13 14:14
 */
public class TolerantStrategyFactory {

    static {
        SpiLoader.load(TolerantStrategy.class);
    }

    /**
     * 默认容错策略
     */
    private static final TolerantStrategy DEFAULT_RETRY_STRATEGY = new FailFastTolerantStrategy();

    /**
     * 获取实例对象
     * @param key
     * @return
     */
    public static TolerantStrategy getInstance(String key){
        return SpiLoader.getInstance(TolerantStrategy.class,key);
    }
}
