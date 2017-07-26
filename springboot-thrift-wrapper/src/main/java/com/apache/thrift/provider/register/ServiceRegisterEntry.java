package com.apache.thrift.provider.register;

import com.apache.thrift.common.ServiceInfo;

/**
 * Created by aca on 2017/7/21.
 */
public interface ServiceRegisterEntry {

    void register(ServiceInfo info) throws Exception;
    void close();
}
