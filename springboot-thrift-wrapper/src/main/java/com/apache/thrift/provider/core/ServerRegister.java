package com.apache.thrift.provider.core;

import com.apache.thrift.common.ConfigProperties;
import com.apache.thrift.common.ServiceDefinition;
import com.apache.thrift.common.ServiceInfo;
import com.apache.thrift.provider.ServerConfig;
import com.apache.thrift.utils.ServiceUtils;
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

    private Map<String, ServiceDiscovery<ServiceInfo>> serviceDiscoveries;
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

            serviceDiscoveries = new HashMap<>();

            instances = new ArrayList<>();

            for (ServiceDefinition def : ServiceDefinition.getDefs()) {

                Class iface = def.getIface();
                if (!ServiceUtils.isTService(iface)) {
                    continue;
                }

                String serviceName = ServiceUtils.getName(iface);
                String group = ServiceUtils.getGroup(iface);
                String version = ServiceUtils.getVersion(iface);

                try {

                    ServiceInstance<ServiceInfo> instance1 = ServiceInstance.<ServiceInfo>builder()
                            .name(serviceName)
                            .port(properties.getPort())
                            .address(StringUtils.isEmpty(properties.getHost()) ? null : properties.getHost())
                            .payload(new ServiceInfo(UUID.randomUUID().toString(), serviceName, group, version))
                            .build();

                    registerService(instance1);
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
        if(serviceDiscoveries != null ) {
            for(ServiceDiscovery<ServiceInfo> serviceDiscovery : serviceDiscoveries.values()) {
                try {
                    serviceDiscovery.close();
                } catch (IOException e) {
                    log.error("destroy service discovery failed", e);
                }
            }
        }
    }

    private ServiceDiscovery<ServiceInfo> getServiceDiscovery(String group) throws Exception {

        if(!serviceDiscoveries.containsKey(group)){

            JsonInstanceSerializer<ServiceInfo> serializer = new JsonInstanceSerializer<>(ServiceInfo.class);
            ServiceDiscovery<ServiceInfo> serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceInfo.class)
                    .client(client)
                    .serializer(serializer)
                    .basePath(group)
                    .build();

            serviceDiscovery.start();
            serviceDiscoveries.put(group, serviceDiscovery);
        }

        return serviceDiscoveries.get(group);
    }

    public void registerService(ServiceInstance<ServiceInfo> serviceInstance) throws Exception {

        ServiceDiscovery<ServiceInfo> serviceDiscovery = getServiceDiscovery(serviceInstance.getPayload().getGroup());
        serviceDiscovery.registerService(serviceInstance);

        log.info("service registered: " + serviceInstance.getName());
    }

    public void unregisterService(ServiceInstance<ServiceInfo> serviceInstance) throws Exception {

        ServiceDiscovery<ServiceInfo> serviceDiscovery = getServiceDiscovery(serviceInstance.getPayload().getGroup());
        serviceDiscovery.unregisterService(serviceInstance);

        log.info("service unregistered: " + serviceInstance.getName());
    }

    public void updateService(ServiceInstance<ServiceInfo> serviceInstance) throws Exception {

        ServiceDiscovery<ServiceInfo> serviceDiscovery = getServiceDiscovery(serviceInstance.getPayload().getGroup());
        serviceDiscovery.updateService(serviceInstance);

        log.info("service updated: " + serviceInstance.getName());
    }
}
