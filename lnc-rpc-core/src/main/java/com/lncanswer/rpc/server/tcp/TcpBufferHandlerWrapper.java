package com.lncanswer.rpc.server.tcp;

import com.lncanswer.rpc.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;



/**
 * @author LNC
 * @version 1.0
 * @description 装饰者模式（使用recordParser对原有的buffer处理能力进行增强）
 * @date 2024/4/12 9:05
 */
public class TcpBufferHandlerWrapper implements Handler<Buffer> {

    //vert.x中内置的RecordParser可以很好的解决半包、粘包问题
    private final RecordParser recordParser;

    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler){
        recordParser = initRecordParser(bufferHandler);
    }

    private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {
        //构造parser
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);

        parser.setOutput(new Handler<Buffer>() {
            //初始化
            int size = -1;
            //一次完整的读取（头和体）
            Buffer resultBuffer = Buffer.buffer();
            @Override
            public void handle(Buffer buffer) {
                if (-1 == size){
                    //读取消息体长度
                    size = buffer.getInt(13);
                    parser.fixedSizeMode(size);
                    //写入头信息到结果
                    resultBuffer.appendBuffer(buffer);
                } else {
                    //写入体信息到结果
                    resultBuffer.appendBuffer(buffer);
                    //已拼接完整的Buffer，执行处理
                    bufferHandler.handle(resultBuffer);
                    //重置一轮
                    parser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    size = -1;
                    resultBuffer = Buffer.buffer();
                }
            }
        });
        return parser;
    }

    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }
}
