package com.apache.thrift.provider.register.impl;

import com.apache.thrift.common.ConfigProperties;
import com.apache.thrift.common.ServiceInfo;
import com.apache.thrift.provider.register.ServiceRegisterEntry;
import com.apache.thrift.utils.ZookeeperUtils;
import lombok.extern.log4j.Log4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aca on 2017/7/24.
 */
@Log4j
public class ZookeeperServiceRegisterEntry implements ServiceRegisterEntry {

    private ConfigProperties properties;

    private Map<String, ServiceDiscovery<ServiceInfo>> serviceDiscoveries;
    private CuratorFramework client;

    public ZookeeperServiceRegisterEntry(ConfigProperties properties) {

        this.properties = properties;

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
    }

    @Override
    public void register(ServiceInfo info) throws Exception {

        String path = ZookeeperUtils.getPath(info.getName(), info.getVersion());

        ServiceInstance<ServiceInfo> instance = ServiceInstance.<ServiceInfo>builder()
                .name(path)
                .port(properties.getPort())
                .address(StringUtils.isEmpty(properties.getHost()) ? null : properties.getHost())
                .payload(info)
                .build();

        registerService(instance);
    }

    @Override
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
}
