package com.lncanswer.rpc.loadbalancer;

/**
 * @author LNC
 * @version 1.0
 * @description 负载均衡器键名常量
 * @date 2024/4/12 21:41
 */
public interface LoadBalancerKeys {

    /**
     * 轮询
     */
    String ROUND_ROBIN = "roundRobin";

    String RANDOM = "random";

    String CONSISTENT_HASH = "consistentHash";
}
