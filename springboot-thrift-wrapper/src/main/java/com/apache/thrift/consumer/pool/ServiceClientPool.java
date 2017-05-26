package com.apache.thrift.consumer.pool;

import com.apache.thrift.consumer.core.ServiceClient;

/**
 * Created by zhuangjiesen on 2017/5/1.
 */
public interface ServiceClientPool {

    ServiceClient getClientInstance(Class iface);
    void recycleClient();
}
