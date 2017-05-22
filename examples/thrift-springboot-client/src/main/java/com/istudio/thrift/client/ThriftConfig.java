package com.istudio.thrift.client;

import com.apache.thrift.consumer.ClientConfig;
import org.springframework.context.annotation.Configuration;

/**
 * Created by ACA on 2017-5-22.
 */
@Configuration
public class ThriftConfig extends ClientConfig {

    public ThriftConfig() {

        registerPackage("com.istudio.thrift.service");
    }
}
