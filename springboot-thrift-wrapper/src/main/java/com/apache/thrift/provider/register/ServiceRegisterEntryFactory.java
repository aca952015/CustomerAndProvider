package com.apache.thrift.provider.register;

import com.apache.thrift.common.ConfigProperties;
import com.apache.thrift.common.Consts;
import com.apache.thrift.provider.register.impl.ZookeeperServiceRegisterEntry;

public class ServiceRegisterEntryFactory {

    public static ServiceRegisterEntry build(ConfigProperties properties) {

        if(Consts.REGISTER_CENTER_ZOOKEEPER.equals(properties.getDiscoveryType())) {

            return new ZookeeperServiceRegisterEntry(properties);
        }

        return null;
    }
}
