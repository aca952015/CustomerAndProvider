package com.apache.thrift.annotation;

import com.apache.thrift.common.Consts;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ACA on 2017/4/14.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TService {
    String group = Consts.SERVICE_BASE_GROUP;
    String version = Consts.SERVICE_BASE_VERSION;
}
