package com.apache.thrift.consumer.proxy;


import com.apache.thrift.consumer.pool.ServiceClientPool;
import com.apache.thrift.utils.ThriftUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by zhuangjiesen on 2017/4/30.
 */
public class JdkThriftClientProxy implements InvocationHandler {

    /*thrift 服务类的iface 类*/
    private Class ifaceClazz;

    private ServiceClientPool serviceClientPool;

    public JdkThriftClientProxy() {

    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {



        Object result = null;


        String serviceName = ThriftUtils.getServiceName(ifaceClazz);

        try {
            Object clientInstance = null;
            clientInstance = serviceClientPool.getClientInstance(serviceName);

            long start = System.currentTimeMillis();

            //方法执行
            result = method.invoke(clientInstance, args);

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


    public Class getIfaceClazz() {
        return ifaceClazz;
    }

    public void setIfaceClazz(Class ifaceClazz) {
        this.ifaceClazz = ifaceClazz;
    }


    public ServiceClientPool getServiceClientPool() {
        return serviceClientPool;
    }

    public void setServiceClientPool(ServiceClientPool serviceClientPool) {
        this.serviceClientPool = serviceClientPool;
    }
}
