package com.apache.thrift.utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.nio.ByteBuffer;

/**
 * Created by ACA on 2017/5/25.
 */
public class KyroUtils {
    private static final Kryo kryo = new Kryo();

    public static ByteBuffer write(Object object, Class type) {

        Output output = new Output(1024);

        kryo.writeObjectOrNull(output, object, type);

        byte[] buffer = output.toBytes();

        return ByteBuffer.wrap(buffer);
    }

    public static Object read(ByteBuffer buffer, Class type) {

        byte[] bufferData = new byte[buffer.capacity()];
        buffer.get(bufferData, 0, bufferData.length);

        Input input = new Input(bufferData);

        return kryo.readObject(input, type);
    }
}
