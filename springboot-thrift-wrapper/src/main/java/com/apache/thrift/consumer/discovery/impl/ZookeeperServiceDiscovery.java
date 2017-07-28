package com.apache.thrift.consumer.discovery.impl;

import com.apache.thrift.common.ConfigProperties;
import com.apache.thrift.common.ServiceInfo;
import com.apache.thrift.consumer.discovery.ServiceDiscovery;
import com.apache.thrift.utils.ServiceUtils;
import com.apache.thrift.utils.ZookeeperUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.log4j.Log4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.strategies.RandomStrategy;

import java.io.Closeable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class ZookeeperServiceDiscovery implements ServiceDiscovery {

    private ConfigProperties properties;
    private CuratorFramework client;
    private Map<String, org.apache.curator.x.discovery.ServiceDiscovery> serviceDiscoveries;
    private final Map<String, ServiceProvider<ServiceInfo>> providers = Maps.newHashMap();
    private final List<Closeable> closeableList = Lists.newArrayList();
    private final Object lock = new Object();

    public ZookeeperServiceDiscovery(ConfigProperties properties) {
        this.properties = properties;

        init();
    }

    @Override
    public ServiceInfo getServiceInfo(Class iface) throws Exception {

        ServiceInstance<ServiceInfo> instance = getInstanceByName(iface);
        if(instance == null) {
            return null;
        }

        return instance.getPayload();
    }


    private void init() {

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

            closeableList.add(client);

            serviceDiscoveries = new HashMap<>();

            log.info("zookeeper server connected.");
        } catch(Exception e) {

            log.error(e);

            close();
        }
    }

    private ServiceInstance<ServiceInfo> getInstanceByName(Class iface) throws Exception {

        String serviceName = ZookeeperUtils.getPath(ServiceUtils.getName(iface), ServiceUtils.getVersion(iface));
        ServiceProvider<ServiceInfo> provider = providers.get(serviceName);
        if (provider == null) {
            synchronized (lock) {
                provider = providers.get(serviceName);
                if (provider == null) {

                    String group = ServiceUtils.getGroup(iface);

                    org.apache.curator.x.discovery.ServiceDiscovery serviceDiscovery = serviceDiscoveries.get(group);
                    if(serviceDiscovery == null) {

                        JsonInstanceSerializer<ServiceInfo> serializer = new JsonInstanceSerializer<>(ServiceInfo.class);
                        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceInfo.class)
                                .client(client)
                                .basePath(group)
                                .serializer(serializer)
                                .build();

                        serviceDiscovery.start();
                        serviceDiscoveries.put(group, serviceDiscovery);
                        closeableList.add(serviceDiscovery);
                    }

                    provider = serviceDiscovery.serviceProviderBuilder().
                            serviceName(serviceName).
                            providerStrategy(new RandomStrategy<>())
                            .build();

                    provider.start();
                    closeableList.add(provider);
                    providers.put(serviceName, provider);
                }
            }
        }

        return provider.getInstance();
    }


    private synchronized void close(){
        for (Closeable closeable : closeableList) {
            CloseableUtils.closeQuietly(closeable);
        }
    }
}
