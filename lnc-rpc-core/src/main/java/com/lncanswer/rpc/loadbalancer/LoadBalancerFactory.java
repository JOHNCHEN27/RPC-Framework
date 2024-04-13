package com.lncanswer.rpc.loadbalancer;

import com.lncanswer.rpc.spi.SpiLoader;

/**
 * @author LNC
 * @version 1.0
 * @description 负载均衡器工厂（工厂模式，用于获取负载均衡器对象）
 * @date 2024/4/12 21:44
 */
public class LoadBalancerFactory {

    static {
        SpiLoader.load(LoadBalancer.class);
    }

    /**
     * 默认负载均衡器（轮询）
     */
    private static final LoadBalancer DEFAULT_LOAD_BALANCER = new RoundRobinLoadBalancer();

    /**
     * 获取实例对象
     * @param key
     * @return
     */
    public static LoadBalancer getInstance(String key){
        return SpiLoader.getInstance(LoadBalancer.class,key);
    }

}
