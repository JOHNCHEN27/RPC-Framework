package com.lncanswer.rpc.loadbalancer;

import com.lncanswer.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author LNC
 * @version 1.0
 * @description 随机负载均衡器 -- 使用Java自带的Random类实现随机抽取即可
 * @date 2024/4/12 21:29
 */
public class RandomLoadBalancer implements LoadBalancer{
    private final Random random = new Random();
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        int size = serviceMetaInfoList.size();
        if (size == 0 ){
            return null;
        }
        //只有1个服务，不用随机
        if (size == 1){
            return serviceMetaInfoList.get(0);
        }
        return serviceMetaInfoList.get(random.nextInt(size));
    }
}
