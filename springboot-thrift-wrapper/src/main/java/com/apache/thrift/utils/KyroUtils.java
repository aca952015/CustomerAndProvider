package com.apache.thrift.utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.esotericsoftware.kryo.io.Output;

import java.nio.ByteBuffer;

/**
 * Created by ACA on 2017/5/25.
 */
public class KyroUtils {
    private static final Kryo kryo = new Kryo();

    public ByteBuffer write(Object object, Class type) {

        ByteBuffer buffer = new ByteBuffer();
        Output output = new ByteBufferOutput()

        kryo.writeObjectOrNull(output, object, type);

        return output;
    }
}
