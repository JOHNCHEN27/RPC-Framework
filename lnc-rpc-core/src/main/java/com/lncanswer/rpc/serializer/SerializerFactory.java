package com.lncanswer.rpc.serializer;

import com.lncanswer.rpc.spi.SpiLoader;


/**
 * @author LNC
 * @version 1.0
 * @description 序列化器工厂（用于获取序列化对象）
 * @date 2024/4/7 16:25
 */
public class SerializerFactory {

//    /**
//     * 序列化映射（用于实现单例模式）
//     */
//    private static final Map<String,Serializer> KEY_SERIALIZER_MAP = new HashMap<String,Serializer>(){{
//        put(SerializerKeys.JDK,new JdkSerializer());
//        put(SerializerKeys.JSON,new JsonSerializer());
//        put(SerializerKeys.KRYO,new KryoSerializer());
//        put(SerializerKeys.HESSIAN,new HessianSerializer());
//    }};
//
//    /**
//     * 默认序列化器
//     */
//    private static final Serializer DEFAUTL_SERIALIZER = KEY_SERIALIZER_MAP.get("jdk");
//
//    /**
//     * 获取实例
//     * @param key
//     * @return
//     */
//    public static Serializer getInstance(String key){
//        //获取key对应的键值序列化器，如果没有则使用默认序列化器
//        return KEY_SERIALIZER_MAP.getOrDefault(key,DEFAUTL_SERIALIZER);
//    }


    //SPI加载指定序列化器对象 --静态代码块 在工厂首次加载时调用
    static  {
       try {
           SpiLoader.load(Serializer.class);
       } catch (Exception e){
           e.printStackTrace();
       }
    }

    /**
     * 默认序列化器 jdk原始序列化器
     */
    private static final Serializer DEFAUTL_SERIALIZER = new JdkSerializer();

    /**
     * 获取实例
     * @param key
     * @return
     */
    public static Serializer getInstance(String key){
        return SpiLoader.getInstance(Serializer.class,key);
    }

}
