package com.apache.thrift.provider;

import lombok.extern.log4j.Log4j;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by ACA on 2017/5/22.
 */
@Log4j(topic = "thrift server holder")
public class ServerHolder implements ApplicationContextAware {

    @Value("${thrift.server.port:9000}")
    private int port;

    private static ApplicationContext context = null;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {

        if (ServerHolder.context == null) {

            ServerHolder.context = context;

            log.info("spring context inited");

            try {
                startServer();
            } catch (TTransportException e) {
                e.printStackTrace();
            }
        }
    }

    private void startServer() throws TTransportException {

        log.info("starting server");

        ServerConfig config = ServerConfig.getInstance();
        Class[] services = config.getServices();
        if (services != null || services.length > 0) {

            final int serverPort = this.port;

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        TServerSocket serverTransport = new TServerSocket(serverPort);

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
                            if(iface.getName().indexOf(Consts.IFACE_NAME) < 0) {
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

                        poolServer.serve();
                    } catch (Exception ex) {
                        log.error(ex);
                    }
                }
            });
            thread.start();
        }
    }

    // 获取applicationContext
    ApplicationContext getContext() {
        return context;
    }

    // 通过name获取 Bean.
    Object getBean(String name) {
        return getContext().getBean(name);
    }

    // 通过class获取Bean.
    <T> T getBean(Class<T> clazz) {
        return getContext().getBean(clazz);
    }

    // 通过name,以及Clazz返回指定的Bean
    <T> T getBean(String name, Class<T> clazz) {
        return getContext().getBean(name, clazz);
    }
}