package com.apache.thrift.consumer.pool.impl;

import com.apache.thrift.common.ConfigProperties;
import com.apache.thrift.common.Consts;
import com.apache.thrift.common.ServiceInfo;
import com.apache.thrift.consumer.pool.SocketBuilder;
import com.apache.thrift.consumer.pool.SocketFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.strategies.RandomStrategy;
import org.apache.thrift.transport.TSocket;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

/**
 * Created by ACA on 2017-5-28.
 */
@Getter
@Setter
@Log4j
public class DiscoverySocketBuilder implements SocketBuilder {

    private ConfigProperties properties;

    private CuratorFramework client;
    private ServiceDiscovery<ServiceInfo> serviceDiscovery;
    private final Map<String, ServiceProvider<ServiceInfo>> providers = Maps.newHashMap();
    private final List<Closeable> closeableList = Lists.newArrayList();
    private final Object lock = new Object();

    @Override
    public TSocket newSocket(Class iface) {

        try {

            ServiceInstance<ServiceInfo> instance = getInstanceByName(iface.getName());

            return SocketFactory.newTSocket(
                    instance.getAddress(),
                    instance.getPort(),
                    properties.getConnectionConnectTimeout(),
                    properties.getConnectionSocketTimeout(),
                    properties.getConnectionTimeout());
        } catch (Exception e) {

            log.error("get instance failed: " + iface.getName());

            return null;
        }
    }

    public DiscoverySocketBuilder() {
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
                    .basePath(Consts.SERVICE_BASEPATH)
                    .serializer(serializer)
                    .build();

            serviceDiscovery.start();
        } catch(Exception e) {

            log.error(e);

            close();
        }
    }


    protected ServiceInstance<ServiceInfo> getInstanceByName(String serviceName) throws Exception {
        ServiceProvider<ServiceInfo> provider = providers.get(serviceName);
        if (provider == null) {
            synchronized (lock) {
                provider = providers.get(serviceName);
                if (provider == null) {
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


    public synchronized void close(){
        for (Closeable closeable : closeableList) {
            CloseableUtils.closeQuietly(closeable);
        }
    }
}