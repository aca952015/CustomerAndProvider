package com.apache.thrift.consumer;

import com.apache.thrift.common.BaseConfig;
import com.apache.thrift.consumer.core.AppThriftServiceManager;
import com.apache.thrift.consumer.core.ThriftSpringFactoryBean;
import com.apache.thrift.consumer.pool.AppThriftConnectionPool;
import com.apache.thrift.consumer.pool.AppThriftServiceClientPool;
import com.apache.thrift.consumer.pool.ConnectionPool;
import com.apache.thrift.consumer.pool.ServiceClientPool;
import com.apache.thrift.consumer.pool.bo.TSocketFactory;
import com.apache.thrift.utils.ThriftUtils;
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
    public TSocketFactory socketFactory() {
        return new TSocketFactory();
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
    public ConnectionPool thriftConnectionPool(TSocketFactory sockerFactory, TTransportFactory transportFactory, TProtocolFactory protocolFactory) {
        AppThriftConnectionPool connectionPool = new AppThriftConnectionPool();
        connectionPool.setSocketFactory(sockerFactory);
        connectionPool.setTransportFactory(transportFactory);
        connectionPool.setProtocolFactory(protocolFactory);

        return connectionPool;
    }

    @Bean
    public ServiceClientPool clientPool(ConnectionPool connectionPool) {
        AppThriftServiceClientPool clientPool = new AppThriftServiceClientPool();
        clientPool.setConnectionPool(connectionPool);

        return clientPool;
    }

    @Bean
    public AppThriftServiceManager thriftServiceManager(ServiceClientPool clientPool) {
        AppThriftServiceManager manager = new AppThriftServiceManager();
        manager.setServiceClientPool(clientPool);

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

            Class iface = ThriftUtils.getIfaceClazz(service.getTypeName());
            if(iface == null){
                continue;
            }

            log.info(iface.getName());

            ThriftSpringFactoryBean factory = new ThriftSpringFactoryBean();
            factory.setAppThriftClientManager(beanFactory.getBean(AppThriftServiceManager.class));
            factory.setServiceIfaceClass(iface);

            beanFactory.registerSingleton(service.getName(), factory);
        }
    }
}
