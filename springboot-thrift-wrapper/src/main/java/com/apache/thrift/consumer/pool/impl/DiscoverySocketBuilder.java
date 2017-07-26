package com.apache.thrift.consumer.pool.impl;

import com.apache.thrift.common.ConfigProperties;
import com.apache.thrift.common.ServiceInfo;
import com.apache.thrift.consumer.core.SocketBuilder;
import com.apache.thrift.consumer.core.SocketFactory;
import com.apache.thrift.consumer.discovery.ServiceDiscovery;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.thrift.transport.TSocket;

/**
 * Created by ACA on 2017-5-28.
 */
@Getter
@Setter
@Log4j
public class DiscoverySocketBuilder implements SocketBuilder {

    private ConfigProperties properties;
    private ServiceDiscovery serviceDiscovery;

    @Override
    public TSocket newSocket(Class iface) {

        try {

            ServiceInfo serviceInfo = serviceDiscovery.getServiceInfo(iface);

            return SocketFactory.newTSocket(
                    serviceInfo.getHost(),
                    serviceInfo.getPort(),
                    properties.getConnectionConnectTimeout(),
                    properties.getConnectionSocketTimeout(),
                    properties.getConnectionTimeout());
        } catch (Exception e) {

            log.error("get instance failed: " + iface.getName());

            return null;
        }
    }

    public DiscoverySocketBuilder() {
    }
}
