package com.apache.thrift.consumer;

import com.apache.thrift.common.BaseConfig;
import com.apache.thrift.common.ServiceDefinition;
import com.apache.thrift.consumer.core.ClientHolder;
import com.apache.thrift.consumer.core.ServiceFactory;
import com.apache.thrift.consumer.core.ServiceManager;
import com.apache.thrift.consumer.core.ServiceClientManager;
import lombok.extern.log4j.Log4j;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TTransportFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;

/**
 * Created by ACA on 2017-5-22.
 */
@Log4j(topic = "thrift client config")
public class ClientConfig extends BaseConfig implements BeanDefinitionRegistryPostProcessor {

    @Bean
    public ClientHolder clientHolder() {
        return new ClientHolder();
    }

    @Bean
    public TTransportFactory transportFactory() {
        return new org.apache.thrift.transport.TFramedTransport.Factory();
    }

    @Bean
    public TProtocolFactory protocolFactory() {
        return new org.apache.thrift.protocol.TCompactProtocol.Factory();
    }

    @Bean
    public ServiceClientManager clientPool(TTransportFactory transportFactory, TProtocolFactory protocolFactory) {
        ServiceClientManager clientPool = new ServiceClientManager();
        clientPool.setTransportFactory(transportFactory);
        clientPool.setProtocolFactory(protocolFactory);

        return clientPool;
    }

    @Bean
    public ServiceManager thriftServiceManager(ServiceClientManager clientPool) {

        ServiceManager manager = new ServiceManager();
        manager.setServiceClientManager(clientPool);

        return manager;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanFactory) throws BeansException {

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        Class[] services = this.getServices();
        if(services == null || services.length == 0) {
            return;
        }

        for(Class service : services) {

            log.info("init service: " + service.getName());

            ServiceDefinition.register(service);

            ServiceFactory factory = new ServiceFactory();
            factory.setServiceManager(beanFactory.getBean(ServiceManager.class));
            factory.setServiceIfaceClass(service);

            beanFactory.registerSingleton(service.getName(), factory);
        }
    }
}
