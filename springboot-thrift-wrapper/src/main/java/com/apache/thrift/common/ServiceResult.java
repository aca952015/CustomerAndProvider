package com.apache.thrift.common;

import com.apache.thrift.utils.KyroUtils;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

/**
 * Created by ACA on 2017/5/25.
 */
public class ServiceResult extends ServiceBase {

    private final Class<?> returnType;

    public ServiceResult(Class<?> returnType) {
        this.returnType = returnType;
    }

    public Object read(TProtocol iprot) throws TException {

        return read(iprot, returnType);
    }

    public void write(TProtocol oprot, Object result) throws TException {

        write(oprot, result, returnType);
    }
}
