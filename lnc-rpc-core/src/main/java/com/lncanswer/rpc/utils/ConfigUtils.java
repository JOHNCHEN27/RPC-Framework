package com.lncanswer.rpc.utils;

import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import cn.hutool.setting.dialect.Props;

/**
 * @author LNC
 * @version 1.0
 * @description 用于读取配置文件并返回配置对象
 * @date 2024/4/6 9:15
 */
public class ConfigUtils {

    /**
     * 加载配置对象
     * @param tClass
     * @param prefix
     * @return
     * @param <T>
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix){
        return loadConfig(tClass,prefix,"");
    }


    /**
     * 加载配置对象，支持区分环境
     * @param tClass
     * @param prefix
     * @param environment 开发环境 如dev、test等
     * @return
     * @param <T>
     */
    public static <T> T loadConfig(Class<T> tClass,String prefix,String environment){
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)){
            configFileBuilder.append("-").append(environment);
        }
        configFileBuilder.append(".properties");
        //利用hutool工具包中的Props扩展API 生成一个配置文件名对应的props对象
        Props props = new Props(new Props(configFileBuilder.toString()));
        //开启配置文件变动时自动加载
        props.autoLoad(true);

        //将从对应的props名的配置文件中读取prefix前缀的属性并返回一个tClass对应的配置类Java对象
        return props.toBean(tClass,prefix);
    }
}
