package com.apache.thrift.provider.register.impl;

import com.apache.thrift.common.ConfigProperties;
import com.apache.thrift.common.Consts;
import com.apache.thrift.common.ServiceInfo;
import com.apache.thrift.utils.EurekaUtils;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.MyDataCenterInstanceConfig;

public class MyEurekarInstanceConfig extends MyDataCenterInstanceConfig implements EurekaInstanceConfig {

    private final ServiceInfo info;
    private final ConfigProperties properties;

    public MyEurekarInstanceConfig(ServiceInfo info, ConfigProperties properties) {

        super(Consts.REGISTER_CENTER_EUREKA_NAMESPACE);

        this.info = info;
        this.properties = properties;
    }

    @Override
    public String getAppname() {

        return EurekaUtils.getPath(info.getName(), info.getVersion());
    }

    @Override
    public String getIpAddress() {

        return info.getHost() + ":" + info.getPort();
    }

    @Override
    public String getAppGroupName() {

        return info.getGroup();
    }

    @Override
    public String getVirtualHostName() {

        return EurekaUtils.getPath(info.getName(), info.getVersion());
    }
}
