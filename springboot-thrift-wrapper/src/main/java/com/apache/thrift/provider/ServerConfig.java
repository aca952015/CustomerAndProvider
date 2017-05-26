package com.apache.thrift.provider;

import com.apache.thrift.common.BaseConfig;
import com.apache.thrift.provider.core.ServerHolder;
import lombok.extern.log4j.Log4j;
import org.springframework.context.annotation.Bean;

/**
 * Created by ACA on 2017/5/22.
 */
@Log4j(topic = "thrift server config")
public class ServerConfig extends BaseConfig {

    @Bean
    public ServerHolder serverHolder() {
        return new ServerHolder(this);
    }
}
