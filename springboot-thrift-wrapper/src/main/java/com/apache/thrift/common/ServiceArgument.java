package com.apache.thrift.common;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ACA on 2017/5/25.
 */
public class ServiceArgument {

    private final Class<?>[] parameterTypes;

    public ServiceArgument(Class<?>[] types) {
        this.parameterTypes = types;
    }

    public List<Object> read(TProtocol iprot) throws TProtocolException {

        List<Object> values = new ArrayList<>();

        return values;
    }
}
