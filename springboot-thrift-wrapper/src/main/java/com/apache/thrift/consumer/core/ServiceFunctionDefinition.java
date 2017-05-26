package com.apache.thrift.consumer.core;

import com.apache.thrift.common.ServiceArgument;
import com.apache.thrift.common.ServiceResult;
import lombok.Getter;

import java.lang.reflect.Method;

/**
 * Created by ACA on 2017/5/26.
 */
@Getter
public class ServiceFunctionDefinition {

    private final String methodName;
    private final Class iface;
    private final Method method;
    private final ServiceArgument input;
    private final ServiceResult output;

    public ServiceFunctionDefinition(String name, Class iface) {

        this.methodName = name;
        this.iface = iface;
        this.method = buildMethod();
        this.input = buildArguments();
        this.output = buildResult();
    }

    private Method buildMethod() {

        Method[] methods = iface.getMethods();
        for(Method method : methods) {
            if(method.getName().equals(this.methodName)) {
                return method;
            }
        }

        return null;
    }

    private ServiceArgument buildArguments() {

        Class<?>[] types = this.method.getParameterTypes();
        return new ServiceArgument(types);
    }

    private ServiceResult buildResult() {

        return new ServiceResult(this.method.getReturnType());
    }
}
