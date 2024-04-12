package com.lncanswer.rpc.protocol;

import lombok.Getter;

/**
 * @author LNC
 * @version 1.0
 * @description 协议消息的状态枚举
 * TODO 枚举需要回头复习
 * @date 2024/4/11 16:30
 */
@Getter
public enum ProtocolMessageStatusEnum {

    OK("ok",20),
    BAD_REQUEST("badRequest",40),
    BAD_RESPONSE("badResponse",50);

    private final String text;

    private final int value;

    ProtocolMessageStatusEnum(String text,int value){
        this.text = text;
        this.value = value;
    }

    /**
     * 根据value获取枚举
     * @param value
     * @return
     */
    public static ProtocolMessageStatusEnum getEnumByValue(int value){
        for (ProtocolMessageStatusEnum anEnum : ProtocolMessageStatusEnum.values()){
            if (anEnum.value == value){
                return anEnum;
            }
        }
        return null;
    }
}
