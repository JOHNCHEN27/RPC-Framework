package com.lncanswer.rpc.proxy;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;


/**
 * @author LNC
 * @version 1.0
 * @description Mock动态代理（JDK动态代理）
 * @date 2024/4/7 9:31
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {
    /**
     * 调用代理
     * @param proxy the proxy instance that the method was invoked on
     *
     * @param method the {@code Method} instance corresponding to
     * the interface method invoked on the proxy instance.  The declaring
     * class of the {@code Method} object will be the interface that
     * the method was declared in, which may be a superinterface of the
     * proxy interface that the proxy class inherits the method through.
     *
     * @param args an array of objects containing the values of the
     * arguments passed in the method invocation on the proxy instance,
     * or {@code null} if interface method takes no arguments.
     * Arguments of primitive types are wrapped in instances of the
     * appropriate primitive wrapper class, such as
     * {@code java.lang.Integer} or {@code java.lang.Boolean}.
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> methodReturnType = method.getReturnType();
        log.info("mock invoke {}",method.getName());
        return getDefaultObject(methodReturnType);
    }

    /**
     * 生成指定类型的默认值对象
     * @param type
     * @return
     */
    private Object getDefaultObject(Class<?> type) {
        //利用faker伪造假数据
        Faker faker = new Faker();
        //判断是否是基本类型
        if (type.isPrimitive()){
            if (type == boolean.class){
                return faker.bool();
            } else if (type == short.class) {
                return  faker.number().numberBetween(Short.MIN_VALUE,Short.MIN_VALUE);
            } else if (type == int.class) {
                return  faker.number().numberBetween(Integer.MIN_VALUE,Integer.MIN_VALUE);
            } else if (type == long.class) {
                return faker.number().numberBetween(Long.MIN_VALUE,Long.MIN_VALUE);
            }
        }  else if (type == String.class){
            return faker.name().fullName();
        } else if (type == List.class){
            return "Random List";
        }
        //对象类型返回null
        return null;
    }
}
