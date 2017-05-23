package com.apache.thrift.consumer;

import com.apache.thrift.common.BaseHolder;
import com.apache.thrift.common.ThriftProperties;
import com.apache.thrift.consumer.pool.ConnectionPool;
import com.apache.thrift.consumer.pool.bo.TSocketFactory;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by ACA on 2017-5-22.
 */
@Log4j(topic = "thrift holder")
public class ClientHolder extends BaseHolder {

    @Autowired
    private ThriftProperties properties;

    @Autowired
    private TSocketFactory socketFactory;

    @Autowired
    private ConnectionPool connectionPool;

    @Override
    protected void doInit() {

        socketFactory.setPort(properties.getPort());
        socketFactory.setHost(properties.getHost());

        connectionPool.init();;
    }
}
