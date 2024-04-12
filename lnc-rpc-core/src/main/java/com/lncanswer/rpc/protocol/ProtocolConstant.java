package com.lncanswer.rpc.protocol;

/**
 * @author LNC
 * @version 1.0
 * @description 协议常量类 --记录了和自定义协议有关的关键信息，比如消息头长度、魔数、版本号
 * @date 2024/4/11 16:26
 */
public interface ProtocolConstant {
    /**
     * 消息头长度
     */
    int MESSAGE_HEADER_LENGTH = 17;

    /**
     * 协议魔数
     */
    byte PROTOCOL_MAGIC = 0x1;

    /**
     * 协议版本号
     */
    byte PROTOCOL_VERSION = 0x1;
}
