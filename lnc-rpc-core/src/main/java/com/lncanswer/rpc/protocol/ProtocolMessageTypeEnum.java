package com.lncanswer.rpc.protocol;

import lombok.Getter;

/**
 * @author LNC
 * @version 1.0
 * @description 协议消息的类型枚举
 * @date 2024/4/11 16:38
 */
@Getter
public enum ProtocolMessageTypeEnum {

    REQUEST(0),
    RESPONSE(1),
    HEART_BEAT(2),
    OTHERS(3);

    private final int key;

    ProtocolMessageTypeEnum(int key){
        this.key = key;
    }

    /**
     * 根据key获取枚举
     * @param key
     * @return
     */
    public static ProtocolMessageTypeEnum getEnumByKey(int key){
        for (ProtocolMessageTypeEnum anEnum : ProtocolMessageTypeEnum.values()){
            if (anEnum.key == key){
                return anEnum;
            }
        }
        return null;
    }
}
