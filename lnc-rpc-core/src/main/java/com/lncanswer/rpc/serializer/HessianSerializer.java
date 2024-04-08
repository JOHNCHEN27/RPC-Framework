package com.lncanswer.rpc.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author LNC
 * @version 1.0
 * @description Hessian序列化器
 * @date 2024/4/7 16:16
 */
public class HessianSerializer implements Serializer{
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HessianOutput ho = new HessianOutput(outputStream);
        ho.writeObject(object);
        return outputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        HessianInput hi = new HessianInput(inputStream);
        return (T) hi.readObject(type);
    }
}
