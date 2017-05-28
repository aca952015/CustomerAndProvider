package com.apache.thrift.consumer.core;


import com.apache.thrift.consumer.pool.ServiceClientPool;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by zhuangjiesen on 2017/4/30.
 */
@Getter
@Setter
public class ClientProxy implements InvocationHandler {

    private Class ifaceClazz;
    private ServiceClientPool serviceClientPool;

    public ClientProxy() {

    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Object result = null;

        try {

            ServiceClient clientInstance = serviceClientPool.getClientInstance(ifaceClazz);

            //方法执行
            result = clientInstance.sendBase(method.getName(), args);
        } catch (Exception e) {
            //异常处理
            // TODO: handle exception

            e.printStackTrace();
        } finally {
            // 回收
            serviceClientPool.recycleClient();
        }

        return result;
    }
}
