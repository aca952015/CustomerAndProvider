package com.apache.thrift.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by ACA on 2017/5/23.
 */
@Getter
@Setter
public class ConfigProperties {

    @Value("${spring.application.name:thrift-app}")
    private String name;

    @Value("${thrift.port:9000}")
    private int port;

    @Value("${thrift.host:}")
    private String host;

    @Value("${thrift.serialization.type:fst}")
    private String serializationType;

    @Value("${thrift.discovery.enabled:false}")
    private boolean discoveryEnabled;

    @Value("${thrift.discovery.host:}")
    private String discoveryHost;

    @Value("${thrift.discovery.type:}")
    private String discoveryType;

    @Value("${thrift.connection.connect.timeout:0}")
    private int connectionConnectTimeout;

    @Value("${thrift.connection.socket.timeout:0}")
    private int connectionSocketTimeout;

    @Value("${thrift.connection.timeout:0}")
    private int connectionTimeout;
}
