package com.apache.thrift.utils;

import com.apache.thrift.common.Consts;

/**
 * Created by zhuangjiesen on 2017/4/30.
 */
public class ThriftUtils {

    public static String getServiceName (Class ifaceClazz){
        String serviceIfaceClassName = ifaceClazz.getName();
        String serviceClassName = serviceIfaceClassName.replace(Consts.IFACE_NAME,"");
        return serviceClassName;
    }

    public static Class getIfaceClazz(String serviceName){
        String serviceIfaceClassName = serviceName + Consts.IFACE_NAME;
        Class clazz = null;
        try {
            clazz = Class.forName(serviceIfaceClassName);
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
        }
        return clazz ;
    }

}
