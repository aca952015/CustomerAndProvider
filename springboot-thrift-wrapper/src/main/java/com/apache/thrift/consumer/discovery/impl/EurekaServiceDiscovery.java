package com.apache.thrift.consumer.discovery.impl;

import com.apache.thrift.common.ConfigProperties;
import com.apache.thrift.common.Consts;
import com.apache.thrift.common.ServiceInfo;
import com.apache.thrift.consumer.discovery.ServiceDiscovery;
import com.apache.thrift.utils.EurekaUtils;
import com.apache.thrift.utils.ServiceUtils;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import lombok.extern.log4j.Log4j;

import java.util.UUID;

@Log4j
public class EurekaServiceDiscovery implements ServiceDiscovery {

    private ConfigProperties properties;
    private ApplicationInfoManager applicationInfoManager;
    private EurekaClient eurekaClient;

    public EurekaServiceDiscovery(ConfigProperties properties) {

        this.properties = properties;

        init();
    }

    private void init() {

        EurekaInstanceConfig instanceConfig = new MyEurekarInstanceConfig(properties);
        InstanceInfo instanceInfo = new EurekaConfigBasedInstanceInfoProvider(instanceConfig).get();

        applicationInfoManager = new ApplicationInfoManager(instanceConfig, instanceInfo);
        eurekaClient = new DiscoveryClient(applicationInfoManager, new MyEurekaClientConfig(properties));
    }

    @Override
    public ServiceInfo getServiceInfo(Class iface) throws Exception {

        String path = EurekaUtils.getPath(ServiceUtils.getName(iface), ServiceUtils.getVersion(iface));

        try {

            InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka(path, false);

            String ipAddress = instanceInfo.getIPAddr();
            String[] pairs = ipAddress.split(":");

            ServiceInfo serviceInfo = new ServiceInfo();
            serviceInfo.setId(UUID.randomUUID().toString());
            serviceInfo.setGroup(instanceInfo.getAppGroupName());
            serviceInfo.setHost(pairs[0]);
            serviceInfo.setPort(pairs.length == 2 ? Integer.valueOf(pairs[1]) : Consts.SERVER_DEFAULT_PORT);
            serviceInfo.setVersion(ServiceUtils.getVersion(iface));

            return serviceInfo;

        } catch (Exception e) {
            log.error("Cannot get an instance of example service to talk to from eureka", e);

            return null;
        }
    }
}
