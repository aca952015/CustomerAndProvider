package com.apache.thrift.provider.register.impl;

import com.apache.thrift.common.ConfigProperties;
import com.apache.thrift.common.ServiceInfo;
import com.apache.thrift.provider.register.ServiceRegisterEntry;
import com.netflix.appinfo.*;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;

public class EurekaServiceRegisterEntry implements ServiceRegisterEntry {

    private static ApplicationInfoManager applicationInfoManager;
    private static EurekaClient eurekaClient;
    private ConfigProperties properties;

    public EurekaServiceRegisterEntry(ConfigProperties properties) {

        this.properties = properties;

        init();
    }

    private void init() {

        ApplicationInfoManager applicationInfoManager = initializeApplicationInfoManager(new MyDataCenterInstanceConfig());
        eurekaClient = initializeEurekaClient(applicationInfoManager, new DefaultEurekaClientConfig());
    }

    private static synchronized ApplicationInfoManager initializeApplicationInfoManager(EurekaInstanceConfig instanceConfig) {

        if (applicationInfoManager == null) {

            InstanceInfo instanceInfo = new EurekaConfigBasedInstanceInfoProvider(instanceConfig).get();
            applicationInfoManager = new ApplicationInfoManager(instanceConfig, instanceInfo);
        }

        return applicationInfoManager;
    }

    private static synchronized EurekaClient initializeEurekaClient(ApplicationInfoManager applicationInfoManager, EurekaClientConfig clientConfig) {

        if (eurekaClient == null) {

            eurekaClient = new DiscoveryClient(applicationInfoManager, clientConfig);
        }

        return eurekaClient;
    }

    @Override
    public void register(ServiceInfo info) throws Exception {

    }

    @Override
    public void close() {

    }
}
