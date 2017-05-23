package com.istudio.thrift.server;

import com.apache.thrift.provider.ServerConfig;
import org.springframework.context.annotation.Configuration;

/**
 * Created by ACA on 2017/5/22.
 */
@Configuration
public class ThriftConfig extends ServerConfig {

    public ThriftConfig() {

        registerPackage("com.istudio.thrift.server.impl");
    }
}
