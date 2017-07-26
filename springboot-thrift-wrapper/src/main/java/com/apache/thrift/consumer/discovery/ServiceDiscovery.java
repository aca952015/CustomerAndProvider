package com.apache.thrift.consumer.discovery;

import com.apache.thrift.common.ServiceInfo;

public interface ServiceDiscovery {

    ServiceInfo getServiceInfo(Class iface) throws Exception;
}
