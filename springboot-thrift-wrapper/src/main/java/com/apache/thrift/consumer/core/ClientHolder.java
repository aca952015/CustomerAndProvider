package com.apache.thrift.consumer.core;

import com.apache.thrift.common.BaseHolder;
import com.apache.thrift.common.ConfigProperties;
import com.apache.thrift.consumer.discovery.ServiceDiscovery;
import com.apache.thrift.consumer.discovery.ServiceDiscoveryFactory;
import com.apache.thrift.consumer.pool.impl.DirectSocketBuilder;
import com.apache.thrift.consumer.pool.impl.DiscoverySocketBuilder;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by ACA on 2017-5-22.
 */
@Log4j(topic = "thrift holder")
public class ClientHolder extends BaseHolder {

    @Autowired
    private ConfigProperties properties;

    @Autowired
    private ServiceClientManager clientManager;

    @Override
    protected void doInit() {

        if(properties.isDiscoveryEnabled()) {

            DiscoverySocketBuilder builder = new DiscoverySocketBuilder();
            builder.setProperties(properties);
            builder.setServiceDiscovery(ServiceDiscoveryFactory.build(properties));

            clientManager.setSocketBuilder(builder);
        } else {

            DirectSocketBuilder builder = new DirectSocketBuilder();
            builder.setProperties(properties);

            clientManager.setSocketBuilder(builder);
        }
    }
}
