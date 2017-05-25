package com.apache.thrift.common;

import org.apache.thrift.protocol.TProtocol;

/**
 * Created by ACA on 2017/5/25.
 */
public class ServiceResult {

    private final Class<?> returnType;

    public ServiceResult(Class<?> returnType) {
        this.returnType = returnType;
    }

    public void write(TProtocol oprot, Object result) {



        oprot.writeBinary();
    }
}
