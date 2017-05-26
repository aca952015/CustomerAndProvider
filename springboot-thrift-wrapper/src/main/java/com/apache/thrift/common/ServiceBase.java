package com.apache.thrift.common;

import com.apache.thrift.utils.FstUtils;
import com.apache.thrift.utils.KryoUtils;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

import java.nio.ByteBuffer;

/**
 * Created by ACA on 2017/5/26.
 */
public abstract class ServiceBase {

    protected Object read(TProtocol iprot, Class type) throws TException {

        if(type.isAssignableFrom(short.class)) {

            return iprot.readI16();
        } else if(type.isAssignableFrom(int.class)) {

            return iprot.readI32();
        } else if(type.isAssignableFrom(long.class)) {

            return iprot.readI64();
        } else if(type.isAssignableFrom(double.class)) {

            return iprot.readDouble();
        } else if(type.isAssignableFrom(boolean.class)) {

            return iprot.readBool();
        } else if(type.isAssignableFrom(String.class)) {

            return iprot.readString();
        } else {

            try {

                ByteBuffer buffer = iprot.readBinary();
                return FstUtils.read(buffer, type);
            } catch(Exception e) {

                e.printStackTrace();

                throw new TException(e.getMessage());
            }
        }
    }

    protected void write(TProtocol oprot, Object value, Class type) throws TException {

        if(type.isAssignableFrom(short.class)) {

            oprot.writeI16((short)value);
        } else if(type.isAssignableFrom(int.class)) {

            oprot.writeI32((int)value);
        } else if(type.isAssignableFrom(long.class)) {

            oprot.writeI64((long)value);
        } else if(type.isAssignableFrom(double.class)) {

            oprot.writeDouble((double)value);
        } else if(type.isAssignableFrom(boolean.class)) {

            oprot.writeBool((boolean)value);
        } else if(type.isAssignableFrom(String.class)) {

            oprot.writeString((String)value);
        } else {

            try {

                oprot.writeBinary(FstUtils.write(value));
            } catch(Exception e) {

                e.printStackTrace();

                throw new TException(e.getMessage());
            }
        }
    }
}
