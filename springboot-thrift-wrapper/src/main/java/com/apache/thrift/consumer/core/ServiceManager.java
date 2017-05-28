package com.apache.thrift.consumer.core;


import com.apache.thrift.consumer.pool.ServiceClientPool;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Proxy;

/**
 * Created by zhuangjiesen on 2017/4/30.
 */
@Getter
@Setter
public class ServiceManager {

    private ServiceClientPool serviceClientPool;

    public <T> T getClient(Class<T> iface) {

        if (!iface.isInterface()) {
            throw new RuntimeException("类型错误");
        }
        T client = null;
        ClientProxy proxyInvocation = new ClientProxy();
        // 代理接口类
        proxyInvocation.setIfaceClazz(iface);
        proxyInvocation.setServiceClientPool(serviceClientPool);
        client = (T) Proxy.newProxyInstance(iface.getClassLoader(), new Class[]{iface}, proxyInvocation);
        return client;
    }
}
