package com.apache.thrift.utils;

import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by ACA on 2017-5-26.
 */
public class FstUtils {
    static FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    public static Object read(ByteBuffer buffer, Class clazz) throws Exception {

        byte[] bufferData = new byte[buffer.remaining()];
        buffer.get(bufferData);

        ByteArrayInputStream stream = new ByteArrayInputStream(bufferData);
        FSTObjectInput in = conf.getObjectInput(stream);
        Object result = in.readObject(clazz);
        // DON'T: in.close(); here prevents reuse and will result in an exception
        stream.close();
        return result;
    }

    public static ByteBuffer write(Object object) throws Exception {

        byte[] data = conf.asByteArray(object);

        return ByteBuffer.wrap(data);
    }
}
