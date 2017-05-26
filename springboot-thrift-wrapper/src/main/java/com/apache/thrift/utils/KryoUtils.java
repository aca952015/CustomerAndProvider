package com.apache.thrift.utils;

import com.apache.thrift.consumer.core.ServiceDefinition;
import com.apache.thrift.consumer.core.ServiceFunctionDefinition;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Created by ACA on 2017/5/25.
 */
public class KryoUtils {

    //private static Kryo kryo = new Kryo();

    static {

        //kryo.setRegistrationRequired(false);


//        kryo.register(GregorianCalendar.class);
//        kryo.register(InvocationHandler.class);
//        kryo.register(BigDecimal.class);
//        kryo.register(BigInteger.class);
//        kryo.register(Pattern.class);
//        kryo.register(BitSet.class);
//        kryo.register(URI.class);
//        kryo.register(UUID.class);
//        kryo.register(HashMap.class);
//        kryo.register(ArrayList.class);
//        kryo.register(LinkedList.class);
//        kryo.register(HashSet.class);
//        kryo.register(TreeSet.class);
//        kryo.register(Hashtable.class);
//        kryo.register(Date.class);
//        kryo.register(Calendar.class);
//        kryo.register(ConcurrentHashMap.class);
//        kryo.register(SimpleDateFormat.class);
//        kryo.register(Vector.class);
//        kryo.register(BitSet.class);
//        kryo.register(StringBuffer.class);
//        kryo.register(StringBuilder.class);
//        kryo.register(Object.class);
//        kryo.register(Object[].class);
//        kryo.register(String[].class);
//        kryo.register(byte[].class);
//        kryo.register(char[].class);
//        kryo.register(int[].class);
//        kryo.register(float[].class);
//        kryo.register(double[].class);

//        for(ServiceDefinition def : ServiceDefinition.getDefs()) {
//
//            for(ServiceFunctionDefinition funcDef : def.getFunctions().values()) {
//
//                Method method = funcDef.getMethod();
//                kryo.register(method.getReturnType());
//
//                for(Class pramClazz : method.getParameterTypes()) {
//
//                    kryo.register(pramClazz);
//                }
//            }
//        }
    }

    public static ByteBuffer write(Object object, Class type) {

        Kryo kryo = new Kryo();
        kryo.setReferences(false);
        kryo.setRegistrationRequired(false);
        kryo.setDefaultSerializer(CompatibleFieldSerializer.class);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output(baos);
        kryo.writeClassAndObject(output, object);
        output.flush();
        output.close();

        byte[] buffer = baos.toByteArray();
        try {
            baos.flush();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ByteBuffer.wrap(buffer);
    }

    public static Object read(ByteBuffer buffer, Class type) {

        Kryo kryo = new Kryo();
        kryo.setReferences(false);
        kryo.setRegistrationRequired(false);
        kryo.setDefaultSerializer(CompatibleFieldSerializer.class);

        byte[] bufferData = new byte[buffer.remaining()];
        buffer.position(0);
        buffer.get(bufferData);

        Input input = new Input(bufferData);

        return kryo.readObject(input, type);
    }
}
