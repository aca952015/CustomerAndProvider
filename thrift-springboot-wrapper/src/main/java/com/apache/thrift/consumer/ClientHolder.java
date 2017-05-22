package com.apache.thrift.consumer;

import com.apache.thrift.common.BaseHolder;
import com.apache.thrift.consumer.pool.bo.TSocketFactory;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by ACA on 2017-5-22.
 */
@Log4j(topic = "thrift holder")
public class ClientHolder extends BaseHolder {

    @Value("${thrift.server.host:127.0.0.1}")
    public String host;

    @Autowired
    private TSocketFactory socketFactory;

    @Override
    protected void doInit() {

        socketFactory.setHost(this.host);
        socketFactory.setPort(this.getPort());
    }
}
