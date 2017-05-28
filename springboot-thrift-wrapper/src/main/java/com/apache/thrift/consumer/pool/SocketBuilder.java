package com.apache.thrift.consumer.pool;

import org.apache.thrift.transport.TSocket;

/**
 * Created by ACA on 2017-5-28.
 */
public interface SocketBuilder {

    TSocket newSocket(Class iface);
}
