package com.apache.thrift.consumer.pool;

import org.apache.thrift.protocol.TProtocol;

/**
 * Created by zhuangjiesen on 2017/4/30.
 */
public class Connection {

    private TProtocol protocol;

    //空转时长
    private long lru;

    private Class iface;

    public TProtocol getProtocol() {
        //设置空转时长
        this.lru = System.currentTimeMillis();
        return protocol;
    }

    public void setProtocol(TProtocol protocol) {
        //设置空转时长
        this.lru = System.currentTimeMillis();
        this.protocol = protocol;
    }

    public long getLru() {
        return lru;
    }

    public void setLru(long lru) {
        this.lru = lru;
    }

    public Class getIface() {
        return iface;
    }

    public void setIface(Class iface) {
        this.iface = iface;
    }
}
