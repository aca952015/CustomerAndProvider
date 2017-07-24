package com.apache.thrift.provider.register;

import com.apache.thrift.common.ConfigProperties;
import com.apache.thrift.common.ServiceDefinition;
import com.apache.thrift.common.ServiceInfo;
import com.apache.thrift.provider.ServerConfig;
import com.apache.thrift.utils.ServiceUtils;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * Created by ACA on 2017-5-28.
 */
@Log4j
public class ServerRegister {

    private ServerConfig config;

    @Autowired
    private ConfigProperties properties;

    @Setter
    private ServerRegisterEntry entry;

    public ServerRegister(ServerConfig config) {
        this.config = config;
    }

    public void init() {

        try {

            log.info("registering services");


            for (ServiceDefinition def : ServiceDefinition.getDefs()) {

                Class iface = def.getIface();
                if (!ServiceUtils.isTService(iface)) {
                    continue;
                }

                String serviceName = ServiceUtils.getName(iface);
                String group = ServiceUtils.getGroup(iface);
                String version = ServiceUtils.getVersion(iface);

                try {

                    ServiceInfo info = new ServiceInfo(UUID.randomUUID().toString(), serviceName, group, version, properties.getHost(), properties.getPort());
                    entry.register(info);
                } catch(Exception e) {

                    log.error("register service failed: " + serviceName, e);
                }
            }
        } catch(Exception e) {
            log.error("register service failed", e);

            close();
        }
    }

    public void close() {

    }

}
