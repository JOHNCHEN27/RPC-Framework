package com.lncanswer.rpc.fault.tolerant;

import com.lncanswer.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author LNC
 * @version 1.0
 * @description 静默处理容错策略实现 -- 遇到异常后，记录一条日志，然后正常返回一个响应对象，就像没有报错
 * @date 2024/4/13 14:07
 */
@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("静默处理异常",e);
        return new RpcResponse();
    }
}
