package com.apache.thrift.provider;

import com.apache.thrift.common.BaseConfig;
import com.apache.thrift.provider.core.ServerContainer;
import com.apache.thrift.provider.core.ServerHolder;
import com.apache.thrift.provider.register.ServiceRegister;
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

    @Bean
    public ServerContainer serverContainer() {
        return new ServerContainer(this);
    }

    @Bean
    public ServiceRegister serverRegister() {
        return new ServiceRegister(this);
    }
}
