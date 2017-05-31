package com.apache.thrift.consumer.core;


import com.apache.thrift.consumer.pool.PooledProtocolFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TTransportFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 实例池，实例复用
 * Created by zhuangjiesen on 2017/4/30.
 */
@Getter
@Setter
@Log4j
public class ServiceClientManager {

    private final Map<Class, GenericObjectPool<TProtocol>> connectionPoolMap;
    private SocketBuilder socketBuilder;
    private TTransportFactory transportFactory;
    private TProtocolFactory protocolFactory;
    private final Object lock = new Object();
    private final GenericObjectPoolConfig config;

    public ServiceClientManager() {
        connectionPoolMap = new HashMap<>();
        config = new GenericObjectPoolConfig();
        config.setMaxWaitMillis(5000);
        config.setMaxIdle(10);
        config.setMaxTotal(50);
    }

    private GenericObjectPool<TProtocol> getConnectionPool(Class iface) {

        GenericObjectPool<TProtocol> connectionPool = connectionPoolMap.get(iface);
        if(connectionPool == null) {
            synchronized (lock) {
                if(connectionPool == null){

                    PooledProtocolFactory factory = new PooledProtocolFactory(iface, socketBuilder, transportFactory, protocolFactory);
                    connectionPool = new GenericObjectPool<>(factory, config);
                    connectionPoolMap.put(iface, connectionPool);
                }
            }
        }

        return connectionPool;
    }

    public ServiceClient getClientInstance(Class iface) {

        ServiceClient clientInstance = null;

        try {

            // 连接池中选择 protocol
            GenericObjectPool<TProtocol> connectionPool = getConnectionPool(iface);
            TProtocol protocol = connectionPool.borrowObject();
            if(!protocol.getTransport().isOpen()){
                protocol.getTransport().open();
            }

            clientInstance = new ServiceClient(iface, protocol);
        } catch (Exception e) {

            //异常处理
            e.printStackTrace();
        } finally {

        }

        return clientInstance;
    }

    public void recycleClient(ServiceClient client) {

        try {

            GenericObjectPool<TProtocol> connectionPool = getConnectionPool(client.getIface());
            connectionPool.returnObject(client.getProt());
        }
        catch (Exception e) {

            log.error("recycle client failed.", e);
        }
    }

    public void destroyClient(ServiceClient client) {

        try {

            client.getProt().getTransport().close();
        }
        catch (Exception e) {

            log.error("destroy client failed.", e);
        }
    }
}
