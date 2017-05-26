package com.apache.thrift.consumer.pool;

import org.apache.thrift.protocol.TProtocol;

/**
 * Created by zhuangjiesen on 2017/5/1.
 */
public interface ConnectionPool {

    TProtocol createNewProtocol();
    TProtocol getProtocol(Class iface);
    void init();
    void recycleProtocol();
    void onDestroy();
}
