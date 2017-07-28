package com.apache.thrift.consumer.discovery.impl;

import com.apache.thrift.common.ConfigProperties;
import com.apache.thrift.common.Consts;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.MyDataCenterInstanceConfig;

public class MyEurekarInstanceConfig extends MyDataCenterInstanceConfig implements EurekaInstanceConfig {

    private final ConfigProperties properties;

    public MyEurekarInstanceConfig(ConfigProperties properties) {

        super(Consts.REGISTER_CENTER_EUREKA_NAMESPACE);

        this.properties = properties;
    }
}
