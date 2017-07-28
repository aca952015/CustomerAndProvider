package com.apache.thrift.provider.register.impl;

import com.apache.thrift.common.ConfigProperties;
import com.apache.thrift.common.ServiceInfo;
import com.apache.thrift.provider.register.ServiceRegisterEntry;
import mousio.etcd4j.EtcdClient;

public class EtcdServiceRegisterEntry implements ServiceRegisterEntry {

    private EtcdClient client;
    private ConfigProperties properties;

    public EtcdServiceRegisterEntry(ConfigProperties properties) {

        this.properties = properties;
    }

    @Override
    public void register(ServiceInfo info) throws Exception {

    }

    @Override
    public void close() {

    }
}
