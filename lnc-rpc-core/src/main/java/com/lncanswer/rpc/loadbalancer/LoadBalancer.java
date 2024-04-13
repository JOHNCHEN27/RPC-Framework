package com.lncanswer.rpc.loadbalancer;

import com.lncanswer.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;


/**
 * @author LNC
 * @version 1.0
 * @description 负载均衡器（消费端使用）
 * @date 2024/4/12 21:22
 */
public interface LoadBalancer {

    /**
     * 选择服务调用
     * @param requestParams 请求参数
     * @param serviceMetaInfoList 可用服务列表
     * @return
     */
    ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
