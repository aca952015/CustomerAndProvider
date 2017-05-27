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

    @Value("${thrift.port:9000}")
    private int port;

    @Value("${thrift.host:127.0.0.1}")
    private String host;

    @Value("${thrift.discover:false")
    private boolean discover;
}
