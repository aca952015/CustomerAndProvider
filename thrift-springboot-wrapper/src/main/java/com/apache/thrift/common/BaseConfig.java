package com.apache.thrift.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ACA on 2017-5-22.
 */
public abstract class BaseConfig {

    private List<Class> services;

    public BaseConfig() {

        this.services =  new ArrayList<>();
    }

    @Bean
    public ThriftProperties thriftProperties() {
        return new ThriftProperties();
    }

    public Class[] getServices() {

        return services.toArray(new Class[]{});
    }

    public void registerClass(Class clazz) {

        services.add(clazz);
    }

    public void registerClasses(Class[] classes) {

        for(Class clazz : classes) {

            registerClass(clazz);
        }
    }

    public void registerPackage(String basePackage) {

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(Object.class));
        registerClasses(scanner.findCandidateComponents(basePackage)
                .stream()
                .map(beanDefinition -> ClassUtils
                        .resolveClassName(beanDefinition.getBeanClassName(), this.getClass().getClassLoader()))
                .collect(Collectors.toList()).toArray(new Class[]{}));
    }
}
