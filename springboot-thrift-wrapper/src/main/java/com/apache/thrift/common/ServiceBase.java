package com.apache.thrift.common;

import com.apache.thrift.utils.KyroUtils;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

import java.nio.ByteBuffer;

/**
 * Created by ACA on 2017/5/26.
 */
public abstract class ServiceBase {

    protected Object read(TProtocol iprot, Class type) throws TException {

        ByteBuffer buffer = iprot.readBinary();

        return KyroUtils.read(buffer, type);
    }

    protected void write(TProtocol oprot, Object result, Class type) throws TException {

        oprot.writeBinary(KyroUtils.write(result, type));
    }
}
