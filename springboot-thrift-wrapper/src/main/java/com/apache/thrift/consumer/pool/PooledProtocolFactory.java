package com.apache.thrift.consumer.pool;


import com.apache.thrift.consumer.core.SocketBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportFactory;

/**
 * Created by zhuangjiesen on 2017/4/30.
 */
@Getter
@Setter
@AllArgsConstructor
public class PooledProtocolFactory implements PooledObjectFactory<TProtocol> {

    private Class iface;
    private SocketBuilder socketBuilder;
    private TTransportFactory transportFactory;
    private TProtocolFactory protocolFactory;

    @Override
    public PooledObject<TProtocol> makeObject() throws Exception {

        TSocket socket = socketBuilder.newSocket(iface);
        TTransport transport = transportFactory.getTransport(socket);
        TProtocol protocol = protocolFactory.getProtocol(transport);

        transport.open();

        return new DefaultPooledObject<>(protocol);
    }

    @Override
    public void destroyObject(PooledObject<TProtocol> p) throws Exception {
        TProtocol tProtocol = p.getObject();
        if(tProtocol.getTransport().isOpen()){
            tProtocol.getTransport().close();
        }
    }

    @Override
    public boolean validateObject(PooledObject<TProtocol> p) {
        // 这里确保返回的是已打开的连接
        TProtocol tProtocol = p.getObject();
        return tProtocol.getTransport().isOpen();
    }

    @Override
    public void activateObject(PooledObject<TProtocol> p) throws Exception {

    }

    @Override
    public void passivateObject(PooledObject<TProtocol> p) throws Exception {

    }
}
