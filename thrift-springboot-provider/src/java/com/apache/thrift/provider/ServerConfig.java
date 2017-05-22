package com.apache.thrift.provider;

import lombok.extern.log4j.Log4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ACA on 2017/5/22.
 */
@Log4j(topic = "thrift server config")
public class ServerConfig {

    private final List<Class> services = new ArrayList<>();
    private static ServerConfig instance;

    public ServerConfig() {

        log.info("init");

        instance = this;
    }

    public static ServerConfig getInstance() {

        return instance;
    }

    public Class[] getServices() {

        return services.toArray(new Class[]{});
    }

    public void registerClass(Class clazz) {

        log.info("register service: " + clazz.getName());

        services.add(clazz);
    }

    public void registerClasses(Class[] classes) {

        for(Class clazz : classes) {

            registerClass(clazz);
        }
    }

    public void registerPackage(String basePackage) {

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Component.class));
        registerClasses(scanner.findCandidateComponents(basePackage)
                .stream()
                .map(beanDefinition -> ClassUtils
                        .resolveClassName(beanDefinition.getBeanClassName(), this.getClass().getClassLoader()))
                .collect(Collectors.toList()).toArray(new Class[]{}));
    }

    @Bean
    public ServerHolder serverHolder() {
        return new ServerHolder();
    }
}
