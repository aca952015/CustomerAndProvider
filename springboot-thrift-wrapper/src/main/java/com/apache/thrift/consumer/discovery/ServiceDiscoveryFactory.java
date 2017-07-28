package com.apache.thrift.consumer.discovery;

import com.apache.thrift.common.ConfigProperties;
import com.apache.thrift.common.Consts;
import com.apache.thrift.consumer.discovery.impl.EurekaServiceDiscovery;
import com.apache.thrift.consumer.discovery.impl.ZookeeperServiceDiscovery;

public class ServiceDiscoveryFactory {

    public static ServiceDiscovery build(ConfigProperties properties) {

        if(Consts.REGISTER_CENTER_ZOOKEEPER.equalsIgnoreCase(properties.getDiscoveryType())) {
            return new ZookeeperServiceDiscovery(properties);
        } else if(Consts.REGISTER_CENTER_EUREKA.equalsIgnoreCase(properties.getDiscoveryType())) {
            return new EurekaServiceDiscovery(properties);
        }

        return null;
    }
}
