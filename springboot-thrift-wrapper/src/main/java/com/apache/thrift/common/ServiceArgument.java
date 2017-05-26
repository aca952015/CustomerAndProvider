package com.apache.thrift.common;

import com.apache.thrift.utils.KyroUtils;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolException;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ACA on 2017/5/25.
 */
public class ServiceArgument extends ServiceBase {

    private final List<Class> parameterTypes;

    public ServiceArgument(Class[] types) {
        this.parameterTypes = Arrays.asList(types);
    }

    public Object[] read(TProtocol iprot) throws TException {

        List<Object> values = new ArrayList<>();

        for(Class type : parameterTypes) {

            values.add(read(iprot, type));
        }

        return values.toArray(new Class[]{});
    }

    public void write(TProtocol oprot, Object[] values) throws TException {

        for(int pos = 0; pos < parameterTypes.size(); pos++) {

            write(oprot, values[pos], parameterTypes.get(pos));
        }
    }
}
