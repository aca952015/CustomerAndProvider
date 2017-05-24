package com.apache.thrift.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by ACA on 2017-5-22.
 */
public abstract class BaseHolder implements ApplicationContextAware {

    private static ApplicationContext context = null;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {

        if (BaseHolder.context == null) {

            BaseHolder.context = context;

            doInit();
        }
    }

    protected abstract void doInit();

    // 获取applicationContext
    protected ApplicationContext getContext() {
        return context;
    }

    // 通过name获取 Bean.
    protected Object getBean(String name) {
        try {
            return getContext().getBean(name);
        }
        catch (Exception e) {
            return null;
        }
    }

    // 通过class获取Bean.
    protected <T> T getBean(Class<T> clazz) {
        try {
            return getContext().getBean(clazz);
        } catch (Exception e) {
            return null;
        }
    }

    // 通过name,以及Clazz返回指定的Bean
    protected <T> T getBean(String name, Class<T> clazz) {
        try {
            return getContext().getBean(name, clazz);
        } catch (Exception e) {
            return null;
        }
    }
}