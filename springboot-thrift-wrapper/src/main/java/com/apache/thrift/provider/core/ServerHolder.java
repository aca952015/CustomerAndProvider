package com.apache.thrift.provider.core;

import com.apache.thrift.common.BaseHolder;
import com.apache.thrift.common.ConfigProperties;
import com.apache.thrift.consumer.core.ServiceDefinition;
import com.apache.thrift.provider.ServerConfig;
import lombok.extern.log4j.Log4j;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by ACA on 2017/5/22.
 */
@Log4j(topic = "thrift server holder")
public class ServerHolder extends BaseHolder {

    private ServerConfig config;

    @Autowired
    private ConfigProperties properties;

    public ServerHolder(ServerConfig config) {
        this.config = config;
    }

    @Override
    protected void doInit() {

        try {
            startServer();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }

    private void startServer() throws TTransportException {

        log.info("starting server");

        Class[] services = this.config.getServices();
        if (services != null || services.length > 0) {

            Thread thread = new Thread(() -> {

                try {

                    // 传输通道 - 非阻塞方式
                    TNonblockingServerSocket serverTransport = new TNonblockingServerSocket(this.properties.getPort());

                    //异步IO，需要使用TFramedTransport，它将分块缓存读取。
                    TNonblockingServer.Args tArgs = new TNonblockingServer.Args(serverTransport);

                    tArgs.transportFactory(new TFramedTransport.Factory());

                    //使用高密度二进制协议
                    tArgs.protocolFactory(new TCompactProtocol.Factory());

                    TMultiplexedProcessor multiProcessor = new TMultiplexedProcessor();

                    for (Class service : services) {

                        //处理器关联业务实现
                        Object target = getBean(service);
                        if (service.getInterfaces().length == 0) {
                            continue;
                        }

                        Class iface = service.getInterfaces()[0];

                        // 反射类型
                        ServiceDefinition.register(iface);

                        String serviceName = iface.getName();

                        ServerProcessor processorObj = new ServerProcessor(iface, target);
                        multiProcessor.registerProcessor(serviceName, processorObj);

                        log.info("service loaded: " + serviceName);

                        if(properties.isDiscover()) {

                            //discoveryClient.
                        }
                    }

                    log.info("start server at port: " + properties.getPort());

                    tArgs.processor(multiProcessor);

                    // 使用非阻塞式IO，服务端和客户端需要指定TFramedTransport数据传输的方式
                    TServer server = new TNonblockingServer(tArgs);
                    server.serve(); // 启动服务

                } catch (Exception ex) {
                    log.error(ex);
                }
            });

            thread.start();
        }
    }
}