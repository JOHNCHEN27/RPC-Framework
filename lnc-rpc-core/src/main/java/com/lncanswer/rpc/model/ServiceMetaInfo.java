package com.lncanswer.rpc.model;

import cn.hutool.core.util.StrUtil;
import com.lncanswer.rpc.constant.RpcConstant;
import lombok.Data;

/**
 * @author LNC
 * @version 1.0
 * @description 注册信息（服务元信息）
 * @date 2024/4/9 10:38
 */
@Data
public class ServiceMetaInfo {

    /**
     * 服务名称
     */
    private String  serviceName;

    /**
     * 服务版本号
     */
    private String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;

    /**
     * 服务地址
     */
    private String serviceAddress;

//    /**
//     * 服务域名
//     */
//    private String serviceHost;
//
//    /**
//     * 服务端口
//     */
//    private String servicePort;

    /**
     * 服务分组（暂未实现）
     */
    private String serviceGroup = "default";


    /**
     * 获取服务键名
     * @return
     */
    public String getServiceKey(){
        //后续可扩展服务分组
        //return String.format("%s:%s:%s",serviceName,serviceVersion,serviceGroup);
        return String.format("%s:%s",serviceName,serviceVersion);
    }

    /**
     * 获取服务注册节点键名
     * @return
     */
    public String getServiceNodeKey(){
        return String.format("%s/%s",getServiceKey(),serviceAddress);
    }

//    public String getServiceAddress(){
//        if (!StrUtil.contains(serviceHost,"http")){
//            return String.format("http://%s:%s",serviceHost,servicePort);
//        }
//        return String.format("%s:%s",serviceHost,servicePort);
//    }
}
