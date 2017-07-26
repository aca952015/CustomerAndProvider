package com.apache.thrift.provider.register;

import com.apache.thrift.common.ConfigProperties;
import com.apache.thrift.common.ServiceDefinition;
import com.apache.thrift.common.ServiceInfo;
import com.apache.thrift.provider.ServerConfig;
import com.apache.thrift.utils.ServiceUtils;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by ACA on 2017-5-28.
 */
@Log4j
public class ServiceRegister {

    private ServerConfig config;

    @Autowired
    private ConfigProperties properties;

    @Setter
    private ServiceRegisterEntry entry;

    public ServiceRegister(ServerConfig config) {
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

        entry.close();
    }
}
