package com.apache.thrift.consumer.pool.impl;

import com.apache.thrift.common.ConfigProperties;
import com.apache.thrift.consumer.core.SocketBuilder;
import com.apache.thrift.consumer.core.SocketFactory;
import lombok.Getter;
import lombok.Setter;
import org.apache.thrift.transport.TSocket;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by ACA on 2017-5-28.
 */
@Getter
@Setter
public class DirectSocketBuilder implements SocketBuilder {

    private ConfigProperties properties;

    @Override
    public TSocket newSocket(Class iface) {

        if(iface == null) {
            return null;
        }

        return SocketFactory.newTSocket(
                properties.getHost(),
                properties.getPort(),
                properties.getConnectionConnectTimeout(),
                properties.getConnectionSocketTimeout(),
                properties.getConnectionTimeout());
    }
}
