package com.apache.thrift.consumer.pool;


import com.apache.thrift.consumer.core.ServiceClient;
import lombok.Getter;
import lombok.Setter;
import org.apache.thrift.protocol.TProtocol;

/**
 * 实例池，实例复用
 * Created by zhuangjiesen on 2017/4/30.
 */
@Getter
@Setter
public class ServiceClientPool {

    private ConnectionPool connectionPool;

    public ServiceClient getClientInstance(Class iface) {

        ServiceClient clientInstance = null;

        try {
            // 连接池中选择 protocol
            TProtocol protocol = connectionPool.getProtocol(iface);
            clientInstance = new ServiceClient(iface, protocol);
        } catch (Exception e) {
            //异常处理
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            // 回收 protocol
        }

        return clientInstance;
    }

    public void recycleClient() {

        connectionPool.recycleProtocol();
    }

    public void onDestroy() {
    }
}
