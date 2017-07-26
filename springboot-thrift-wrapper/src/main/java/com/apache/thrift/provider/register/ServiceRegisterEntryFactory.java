package com.apache.thrift.provider.register;

import com.apache.thrift.common.ConfigProperties;
import com.apache.thrift.common.Consts;
import com.apache.thrift.provider.register.impl.EurekaServiceRegisterEntry;
import com.apache.thrift.provider.register.impl.ZookeeperServiceRegisterEntry;

public class ServiceRegisterEntryFactory {

    public static ServiceRegisterEntry build(ConfigProperties properties) {

        if(Consts.REGISTER_CENTER_ZOOKEEPER.equalsIgnoreCase(properties.getDiscoveryType())) {

            return new ZookeeperServiceRegisterEntry(properties);
        } else if(Consts.REGISTER_CENTER_EUREKA.equalsIgnoreCase(properties.getDiscoveryType())) {

            return new EurekaServiceRegisterEntry(properties);
        }

        return null;
    }
}
