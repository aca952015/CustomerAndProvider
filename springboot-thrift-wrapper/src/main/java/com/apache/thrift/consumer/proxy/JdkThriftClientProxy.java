package com.apache.thrift.consumer.proxy;


import com.apache.thrift.consumer.core.ServiceClient;
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
public class JdkThriftClientProxy implements InvocationHandler {

    /*thrift 服务类的iface 类*/
    private Class ifaceClazz;
    private ServiceClientPool serviceClientPool;

    public JdkThriftClientProxy() {

    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Object result = null;

        try {
            ServiceClient clientInstance = serviceClientPool.getClientInstance(ifaceClazz);

            long start = System.currentTimeMillis();

            //方法执行
            result = clientInstance.sendBase(method.getName(), args);

            //执行时间
            long invokeTime = System.currentTimeMillis() - start;

//			System.out.println("result : "+result);
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
