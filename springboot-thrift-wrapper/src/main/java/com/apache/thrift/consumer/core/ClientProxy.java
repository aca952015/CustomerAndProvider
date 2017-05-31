package com.apache.thrift.consumer.core;


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
    private ServiceClientManager serviceClientManager;

    public ClientProxy() {

    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Object result = null;
        ServiceClient clientInstance = serviceClientManager.getClientInstance(ifaceClazz);

        try {

            //方法执行
            result = clientInstance.sendBase(method.getName(), args);
        } catch (Exception e) {

            //异常处理
            e.printStackTrace();

            serviceClientManager.destroyClient(clientInstance);
        } finally {

            // 回收
            serviceClientManager.recycleClient(clientInstance);
        }

        return result;
    }
}
