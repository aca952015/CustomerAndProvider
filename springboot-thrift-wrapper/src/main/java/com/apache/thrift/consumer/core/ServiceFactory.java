package com.apache.thrift.consumer.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;

/**
 * Created by zhuangjiesen on 2017/3/17.
 */
@Getter
@Setter
public class ServiceFactory implements FactoryBean {

    //客户端获取实例
    private ServiceManager serviceManager;

    //服务 Iface 接口类
    private Class serviceIfaceClass;

    // 是否单例
    private boolean isSingleton = true;

    @Override
    public Object getObject() throws Exception {
        return serviceManager.getClient(serviceIfaceClass);
    }

    @Override
    public Class getObjectType() {
        return serviceIfaceClass;
    }
}
