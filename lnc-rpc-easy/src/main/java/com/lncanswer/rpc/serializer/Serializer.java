package com.lncanswer.rpc.serializer;

import java.io.IOException;

/**
 * @author LNC
 * @version 1.0
 * @description 序列化器接口
 * @date 2024/4/2 22:38
 */
public interface Serializer {

    /**
     * 序列化 将java对象序列化可传输的字节数组
     * @param object
     * @return
     * @param <T>
     * @throws IOException
     */
    <T> byte[] serialize(T object) throws IOException;


    /**
     * 反序列化， 将序列化存储的字节数组 转化为java对象
     * @param bytes
     * @param type
     * @return
     * @param <T>
     * @throws IOException
     */
    <T> T deserialize(byte[] bytes, Class<T> type) throws IOException;
}
