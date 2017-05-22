package com.apache.thrift.provider;

import com.apache.thrift.common.BaseHolder;
import com.apache.thrift.common.Consts;
import lombok.extern.log4j.Log4j;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

/**
 * Created by ACA on 2017/5/22.
 */
@Log4j(topic = "thrift server holder")
public class ServerHolder extends BaseHolder {

    private ServerConfig config;

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
                    TServerSocket serverTransport = new TServerSocket(getPort());

                    // 设置二进制协议工厂
                    TBinaryProtocol.Factory protocolFactory = new TBinaryProtocol.Factory();

                    // 使用线程池服务模型
                    TThreadPoolServer.Args poolArgs = new TThreadPoolServer.Args(serverTransport);

                    for (Class service : services) {

                        //处理器关联业务实现
                        Object target = getBean(service);
                        if (service.getInterfaces().length == 0) {
                            continue;
                        }

                        Class iface = service.getInterfaces()[0];
                        if (iface.getName().indexOf(Consts.IFACE_NAME) < 0) {
                            continue;
                        }

                        String ifaceName = iface.getName();
                        String serviceName = ifaceName.substring(0, ifaceName.lastIndexOf(Consts.IFACE_NAME));

                        Class processorClazz = Class.forName(serviceName.concat(Consts.PROCESS_NAME));
                        Object processorObj = processorClazz.getConstructor(iface).newInstance(iface.cast(target));
                        if (processorObj instanceof TProcessor) {
                            TProcessor processor = (TProcessor) processorObj;
                            poolArgs.processor(processor);

                            log.info("service loaded: " + service.getName());
                        }
                    }

                    poolArgs.protocolFactory(protocolFactory);
                    TServer poolServer = new TThreadPoolServer(poolArgs);

                    log.info("start server at port: " + getPort());

                    poolServer.serve();
                } catch (Exception ex) {
                    log.error(ex);
                }
            });

            thread.start();
        }
    }
}