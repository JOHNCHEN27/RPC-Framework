package com.lncanswer.rpc;

import com.lncanswer.rpc.config.RegistryConfig;
import com.lncanswer.rpc.config.RpcConfig;
import com.lncanswer.rpc.constant.RpcConstant;
import com.lncanswer.rpc.registry.Registry;
import com.lncanswer.rpc.registry.RegistryFactory;
import com.lncanswer.rpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author LNC
 * @version 1.0
 * @description Rpc框架应用
 * 相当于holder，存放了项目全局用到的变量，双检锁单例模式实现
 * @date 2024/4/6 9:32
 */
@Slf4j
public class RpcApplication {
    //volatile关键字可以保证变量的可见性
    private static volatile RpcConfig rpcConfig;

    /**
     * 框架初始化，支持传入自定义配置
     * @param newRpcConfig
     */
    public static void init(RpcConfig newRpcConfig){
        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", newRpcConfig.toString());
        //注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init,config = {}",registryConfig);

        //创建并注册Shutdown Hook，JVM退出执行操作 程序正常退出时会执行注册中心的destroy方法
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    /**
     * 初始化
     */
    public static void init(){
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e){
            //配置加载失败，使用默认值
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 获取配置 采用的是单例模式 --双重检测锁方式实现
     * @return
     */
    public static RpcConfig getRpcConfig(){
        if (rpcConfig == null){
            synchronized (RpcApplication.class){
                if (rpcConfig == null){
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
