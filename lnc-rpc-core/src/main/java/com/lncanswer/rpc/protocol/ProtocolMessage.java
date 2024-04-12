package com.lncanswer.rpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LNC
 * @version 1.0
 * @description 协议消息结构
 * 将消息头单独封装一个内部类，消息体可以使用泛型类
 * @date 2024/4/11 16:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMessage<T> {

    /**
     * 消息头
     */
    private Header header;

    /**
     * 消息体(请求或响应对象)
     */
    private T body;

    /**
     * 协议消息头 --使用内部类方式
     */
    @Data
    public static class Header{

        /**
         * 魔数，保证安全性
         */
        private byte magic;

        /**
         * 版本号
         */
        private byte version;

        /**
         * 序列化器
         */
        private byte serializer;

        /**
         * 消息类型（请求/响应）
         */
        private byte type;

        /**
         * 状态
         */
        private byte status;

        /**
         * 请求id -- TCP协议中 id标识某个请求
         */
        private long requestId;

        /**
         * 消息体长度
         */
        private int bodyLength;
    }



}
