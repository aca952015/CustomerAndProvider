package com.apache.thrift.consumer.pool;


import com.apache.thrift.common.Consts;
import com.apache.thrift.consumer.core.ServiceClient;
import com.apache.thrift.consumer.core.ThriftClient;
import com.apache.thrift.utils.ThriftUtils;
import org.apache.thrift.protocol.TProtocol;

import java.lang.reflect.Constructor;

/**
 *
 * 实例池，实例复用
 * Created by zhuangjiesen on 2017/4/30.
 */
public class AppThriftServiceClientPool implements ServiceClientPool {

    private ConnectionPool connectionPool;

    public ServiceClient getClientInstance(Class iface){

        ServiceClient clientInstance = null;

        try {
            // 连接池中选择 protocol
            TProtocol protocol = connectionPool.getProtocol(iface);


        } catch (Exception e) {
            //异常处理
            // TODO: handle exception

            e.printStackTrace();
        } finally {
            // 回收 protocol
        }

        return clientInstance;
    }


    public void recycleClient(){

        connectionPool.recycleProtocol();
    }


    public void onDestroy(){


    }


    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }

    public void setConnectionPool(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }
}
