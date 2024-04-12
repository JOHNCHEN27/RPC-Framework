package com.lncanswer.rpc.protocol;


import com.lncanswer.rpc.serializer.Serializer;
import com.lncanswer.rpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

/**
 * @author LNC
 * @version 1.0
 * @description 消息编码器 -- 依次向 buffer缓冲区写入消息对象里的字段
 * 将发送的请求信息转化为TCP需要的Buffer格式
 * @date 2024/4/11 18:13
 */
public class ProtocolMessageEncoder {

    /**
     * 编码
     * @param protocolMessage
     * @return
     * @throws Exception
     */
    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws Exception{
        if (protocolMessage == null || protocolMessage.getHeader() == null){
            return Buffer.buffer();
        }
        ProtocolMessage.Header header =  protocolMessage.getHeader();
        //依次向缓冲区写入字节
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());

        //获取序列化器
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (serializerEnum == null){
            throw new RuntimeException("序列化协议不存在");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());
        //写入body长度和数据
        buffer.appendInt(bodyBytes.length);
        buffer.appendBytes(bodyBytes);
        return buffer;
    }
}
