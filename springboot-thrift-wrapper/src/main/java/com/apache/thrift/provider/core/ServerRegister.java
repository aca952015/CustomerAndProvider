package com.apache.thrift.provider.core;

import com.apache.thrift.common.ConfigProperties;
import com.apache.thrift.common.Consts;
import com.apache.thrift.common.ServiceDefinition;
import com.apache.thrift.common.ServiceInfo;
import com.apache.thrift.provider.ServerConfig;
import lombok.extern.log4j.Log4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by ACA on 2017-5-28.
 */
@Log4j
public class ServerRegister {

    private ServerConfig config;

    @Autowired
    private ConfigProperties properties;

    private ServiceDiscovery<ServiceInfo> serviceDiscovery;
    private CuratorFramework client;
    private List<ServiceInstance<ServiceInfo>> instances;

    public ServerRegister(ServerConfig config) {
        this.config = config;
    }

    public void init() {

        try {

            log.info("registering services");

            String connectString = properties.getDiscoveryHost();
            int baseSleepTimeMs = 1000; // 初始sleep时间
            int maxRetries = 100; // 最大重试次数
            int maxSleepMs = 25000; // 最大sleep时间
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries, maxSleepMs);
            int sessionTimeoutMs = 10000;
            int connectionTimeoutMs = 10000;

            client = CuratorFrameworkFactory.builder()
                    .connectString(connectString)
                    .retryPolicy(retryPolicy)
                    .sessionTimeoutMs(sessionTimeoutMs)
                    .connectionTimeoutMs(connectionTimeoutMs)
                    .build();
            client.start();

            log.info("zookeeper server connected.");

            JsonInstanceSerializer<ServiceInfo> serializer = new JsonInstanceSerializer<>(ServiceInfo.class);
            serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceInfo.class)
                    .client(client)
                    .serializer(serializer)
                    .basePath(Consts.SERVICE_BASEPATH)
                    .build();

            serviceDiscovery.start();

            log.info("service discovery started.");

            instances = new ArrayList<>();

            for (ServiceDefinition def : ServiceDefinition.getDefs()) {

                String serviceName = def.getIface().getName();

                ServiceInstance<ServiceInfo> instance1 = ServiceInstance.<ServiceInfo>builder()
                        .name(serviceName)
                        .port(properties.getPort())
                        .payload(new ServiceInfo(UUID.randomUUID().toString()))
                        .build();

                registerService(instance1);
            }
        } catch(Exception e) {
            log.error(e);

            close();
        }
    }

    public void close() {
        if(serviceDiscovery != null ) {
            try {
                serviceDiscovery.close();
            } catch (IOException e) {
                log.error(e);
            }
        }
    }

    public void registerService(ServiceInstance<ServiceInfo> serviceInstance) throws Exception {
        serviceDiscovery.registerService(serviceInstance);

        log.info("service registered: " + serviceInstance.getName());
    }

    public void unregisterService(ServiceInstance<ServiceInfo> serviceInstance) throws Exception {
        serviceDiscovery.unregisterService(serviceInstance);

        log.info("service unregistered: " + serviceInstance.getName());
    }

    public void updateService(ServiceInstance<ServiceInfo> serviceInstance) throws Exception {
        serviceDiscovery.updateService(serviceInstance);

        log.info("service updated: " + serviceInstance.getName());
    }
}
