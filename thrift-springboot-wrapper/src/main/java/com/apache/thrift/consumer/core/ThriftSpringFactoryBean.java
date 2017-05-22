package com.apache.thrift.consumer.core;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by zhuangjiesen on 2017/3/17.
 */
public class ThriftSpringFactoryBean implements FactoryBean {

    //客户端获取实例
    private AppThriftServiceManager appThriftClientManager;

    //服务 Iface 接口类
    private Class serviceIfaceClass;

    // 是否单例
    private boolean isSingleton = true;

    public Object getObject() throws Exception {
        System.out.println("ThriftSpringFactoryBean  getObject ...");

        return appThriftClientManager.getClient(serviceIfaceClass);
    }

    public Class getObjectType() {
        return serviceIfaceClass;
    }

    public boolean isSingleton() {
        return isSingleton;
    }


    public void setSingleton(boolean singleton) {
        isSingleton = singleton;
    }

    public AppThriftServiceManager getAppThriftClientManager() {
        return appThriftClientManager;
    }

    public void setAppThriftClientManager(AppThriftServiceManager appThriftClientManager) {
        this.appThriftClientManager = appThriftClientManager;
    }

    public Class getServiceIfaceClass() {
        return serviceIfaceClass;
    }

    public void setServiceIfaceClass(Class serviceIfaceClass) {
        this.serviceIfaceClass = serviceIfaceClass;
    }
}
