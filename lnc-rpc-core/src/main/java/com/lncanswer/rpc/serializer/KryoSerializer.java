package com.lncanswer.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author LNC
 * @version 1.0
 * @description Kryo序列化器
 * Kryo本身是线程不安全的，所以需要使用ThreadLocal保证每一个线程有一个单独的Kryo对象实例
 * @date 2024/4/7 15:58
 */
public class KryoSerializer implements Serializer{

    /**
     * 使用ThreadLocal保证每一个线程有一个单独的Kryo对象实例
     */
    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL =ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        //设置动态序列化和反序列化类，不提前注册所有类（可能有安全问题）
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public <T> byte[] serialize(T object){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Output output = new Output(outputStream);
        KRYO_THREAD_LOCAL.get().writeObject(output,object);
        output.close();
        return outputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(inputStream);
        T result = KRYO_THREAD_LOCAL.get().readObject(input, type);
        input.close();
        return result;
    }
}
