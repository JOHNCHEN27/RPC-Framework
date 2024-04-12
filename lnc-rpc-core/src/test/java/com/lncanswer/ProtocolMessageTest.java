package com.lncanswer;

import cn.hutool.core.util.IdUtil;
import com.lncanswer.rpc.constant.RpcConstant;
import com.lncanswer.rpc.model.RpcRequest;
import com.lncanswer.rpc.protocol.*;
import io.vertx.core.buffer.Buffer;
import org.checkerframework.checker.units.qual.C;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * @author LNC
 * @version 1.0
 * @description
 * @date 2024/4/11 18:43
 */
public class ProtocolMessageTest {

    @Test
    public void testEncodeAndDecode() throws Exception {
        //构造消息
        ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
        header.setSerializer((byte) ProtocolMessageSerializerEnum.JDK.getKey());
        header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
        header.setStatus((byte) ProtocolMessageStatusEnum.OK.getValue());
        header.setRequestId(IdUtil.getSnowflakeNextId());
        header.setBodyLength(0);
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceName("myService");
        rpcRequest.setServiceMethod("myMethod");
        rpcRequest.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        rpcRequest.setParameterTypes(new Class[]{String.class});
        rpcRequest.setArgs(new Object[]{"aaa","bbb"});
        protocolMessage.setHeader(header);
        protocolMessage.setBody(rpcRequest);

        Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
        ProtocolMessage<?> message = ProtocolMessageDecoder.decode(encodeBuffer);
        Assert.assertNotNull(message);
    }
}
