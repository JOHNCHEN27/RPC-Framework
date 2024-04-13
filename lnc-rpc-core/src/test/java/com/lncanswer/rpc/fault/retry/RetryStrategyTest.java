package com.lncanswer.rpc.fault.retry;

import com.lncanswer.rpc.model.RpcResponse;
import org.junit.Test;

/**
 * @author LNC
 * @version 1.0
 * @description 重试策略测试
 * @date 2024/4/13 13:15
 */
public class RetryStrategyTest {

   RetryStrategy retryStrategy =  new NoRetryStrategy();

   @Test
    public void doRetry(){
       try {
           RpcResponse response = retryStrategy.doRetry(() -> {
               System.out.println("测试重试");
               throw new RuntimeException("模拟重试失败");
           });
           System.out.println(response);
       } catch (Exception e){
           System.out.println("重试多次失败");
           e.printStackTrace();
       }
   }
}
