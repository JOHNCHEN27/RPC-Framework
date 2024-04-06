package com.lncanswer.rpc.serializer;

import java.io.*;

/**
 * @author LNC
 * @version 1.0
 * @description 基于Java自带的序列化器实现JdkSerializer
 * @date 2024/4/2 22:43
 */
public class JdkSerializer implements Serializer{

    /**
     * 序列化
     * @param object
     * @return
     * @param <T>
     * @throws IOException
     */
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        return outputStream.toByteArray();
    }

    /**
     * 反序列化
     * @param bytes
     * @param type
     * @return
     * @param <T>
     * @throws IOException
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        try{
            return (T) objectInputStream.readObject();
        } catch (ClassNotFoundException e){
            throw new RuntimeException();
        } finally {
            objectInputStream.close();
        }

    }
}
