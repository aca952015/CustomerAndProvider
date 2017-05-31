package com.apache.thrift.consumer.core;

import org.apache.thrift.transport.TSocket;

/**
 * Created by zhuangjiesen on 2017/4/30.
 */
public class SocketFactory {

    public static TSocket newTSocket(String host, int port, int connectTomeout, int socketTimeout, int timeout) {

        TSocket socket = new TSocket(host, port);
        socket.setConnectTimeout(connectTomeout);
        socket.setSocketTimeout(socketTimeout);
        socket.setTimeout(timeout);

        return socket;
    }
}
