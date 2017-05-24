package com.apache.thrift.consumer.pool;


import com.apache.thrift.common.Consts;
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


    public Object getClientInstance(String serviceName){



        Object clientInstance = null;
        try {

            String serviceIfaceClassName = ThriftUtils.getIfaceClazz(serviceName).getName();

            String serviceClassName = serviceIfaceClassName.replace(Consts.IFACE_NAME,"");

            // 连接池中选择 protocol
            TProtocol protocol = connectionPool.getProtocol(serviceClassName);

            long start = System.currentTimeMillis();

            String serviceClientClassName = serviceIfaceClassName.replace(Consts.IFACE_NAME,Consts.CLIENT_NAME);
            Class clientClazz = Class.forName(serviceClientClassName);
            Constructor constructor = clientClazz.getConstructor(TProtocol.class);
            // 初始化服务实例 client
            clientInstance = constructor.newInstance(protocol);



            //封装成对象
            ThriftClient thriftClient = new ThriftClient();
            thriftClient.setServiceIfaceClassName(serviceIfaceClassName);
            thriftClient.setServiceClassName(serviceClassName);
            thriftClient.setServiceClientClassName(serviceClientClassName);
            thriftClient.setClientClazz(clientClazz);

            //执行时间
            long invokeTime = System.currentTimeMillis() - start;

//			System.out.println("result : "+result);
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
