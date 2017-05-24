package com.apache.thrift.consumer.pool;

import org.apache.thrift.protocol.TProtocol;

/**
 * Created by zhuangjiesen on 2017/5/1.
 */
public interface ConnectionPool {


    TProtocol createNewProtocol();
    TProtocol getProtocol(String serviceName);
    void init();
    void recycleProtocol();
    void onDestroy();

}
