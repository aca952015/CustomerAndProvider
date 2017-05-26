package com.apache.thrift.consumer.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;

/**
 * Created by zhuangjiesen on 2017/3/17.
 */
@Getter
@Setter
public class ThriftSpringFactoryBean implements FactoryBean {

    //客户端获取实例
    private AppThriftServiceManager appThriftClientManager;

    //服务 Iface 接口类
    private Class serviceIfaceClass;

    // 是否单例
    private boolean isSingleton = true;

    @Override
    public Object getObject() throws Exception {
        return appThriftClientManager.getClient(serviceIfaceClass);
    }

    @Override
    public Class getObjectType() {
        return serviceIfaceClass;
    }
}
