package com.apache.thrift.common;

import lombok.Getter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ACA on 2017/5/26.
 */
@Getter
public class ServiceDefinition {

    private final static Map<Class, ServiceDefinition> defs = new HashMap<>();

    private final Class iface;
    private final Map<String, ServiceFunctionDefinition> functions;

    public ServiceDefinition(Class iface) {
        this.iface = iface;
        this.functions = resolveFunctions();
    }

    private Map<String, ServiceFunctionDefinition> resolveFunctions() {

        Map<String, ServiceFunctionDefinition> map = new HashMap<>();

        Method[] methods = iface.getMethods();
        for (Method method : methods) {

            String name = method.getName();

            map.put(name, new ServiceFunctionDefinition(name, iface));
        }

        return map;
    }

    public ServiceFunctionDefinition getFunction(String methodName) {

        if(!this.functions.containsKey(methodName)) {

            return null;
        }

        return this.functions.get(methodName);
    }

    public static ServiceDefinition getDef(Class iface) {

        if(!defs.containsKey(iface)) {

            return null;
        }

        return defs.get(iface);
    }

    public static ServiceDefinition[] getDefs() {
        return defs.values().toArray(new ServiceDefinition[]{});
    }

    public static void register(Class iface) {

        if(!defs.containsKey(iface)) {

            defs.put(iface, new ServiceDefinition(iface));
        }
    }
}
