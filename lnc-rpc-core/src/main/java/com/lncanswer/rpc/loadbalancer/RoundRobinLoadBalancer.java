package com.lncanswer.rpc.loadbalancer;

import com.lncanswer.rpc.model.ServiceMetaInfo;
import org.checkerframework.checker.units.qual.A;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author LNC
 * @version 1.0
 * @description 轮询负载均衡器 --使用 JUC包的AtomicInteger实现原子计数器，防止并发冲突问题
 * @date 2024/4/12 21:24
 */
public class RoundRobinLoadBalancer implements LoadBalancer{

    /**
     * 当前轮询下标
     */
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
       if (serviceMetaInfoList.isEmpty()){
           return null;
       }
       //只有一个服务，无需轮询
        int size = serviceMetaInfoList.size();
       if (size == 1){
           return serviceMetaInfoList.get(0);
       }

       int index = currentIndex.getAndIncrement() & size;
        return serviceMetaInfoList.get(index);
    }
}
