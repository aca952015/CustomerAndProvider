package com.apache.thrift.provider.core;

import com.apache.thrift.common.BaseHolder;
import com.apache.thrift.common.ConfigProperties;
import com.apache.thrift.common.Consts;
import com.apache.thrift.common.ServiceDefinition;
import com.apache.thrift.provider.ServerConfig;
import com.apache.thrift.provider.register.ServerRegister;
import com.apache.thrift.provider.register.impl.ZookeeperServerRegisterEntry;
import lombok.extern.log4j.Log4j;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by ACA on 2017/5/22.
 */
@Log4j(topic = "thrift server holder")
public class ServerHolder extends BaseHolder {

    private ServerConfig config;

    @Autowired
    private ServerContainer container;

    @Autowired
    private ServerRegister register;

    @Autowired
    private ConfigProperties properties;

    public ServerHolder(ServerConfig config) {
        this.config = config;
    }

    @Override
    protected void doInit() {

        try {
            startServer();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }

    private void startServer() throws TTransportException {

        log.info("starting server");

        Class[] services = this.config.getServices();
        if (services != null || services.length > 0) {

            TMultiplexedProcessor multiProcessor = new TMultiplexedProcessor();

            for (Class service : services) {

                // 处理器关联业务实现
                if (service.getInterfaces().length == 0) {
                    continue;
                }

                Class iface = service.getInterfaces()[0];

                // 反射类型
                ServiceDefinition.register(iface);

                String serviceName = iface.getName();

                Object target = getBean(service);

                ServerProcessor processorObj = new ServerProcessor(iface, target);
                multiProcessor.registerProcessor(serviceName, processorObj);

                log.info("service loaded: " + serviceName);
            }

            container.serve(multiProcessor);

            if(properties.isDiscoveryEnabled()) {

                if(Consts.REGISTER_CENTER_ZOOKEEPER.equals(properties.getDiscoveryType())) {
                    register.setEntry(new ZookeeperServerRegisterEntry(properties));
                    register.init();
                }
            }
        }
    }
}