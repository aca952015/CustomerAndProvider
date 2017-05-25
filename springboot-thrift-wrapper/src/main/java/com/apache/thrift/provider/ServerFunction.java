package com.apache.thrift.provider;

import com.apache.thrift.common.ServiceArgument;
import com.apache.thrift.common.ServiceResult;
import lombok.extern.log4j.Log4j;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by ACA on 2017/5/25.
 */
@Log4j
public class ServerFunction {

    private final String methodName;
    private final Class iface;
    private final Object target;
    private final Method method;
    private final ServiceArgument input;
    private final ServiceResult output;

    public ServerFunction(String methodName, Class iface, Object target) {

        this.methodName = methodName;
        this.iface = iface;
        this.target = target;
        this.method = buildMethod();
        this.input = buildInputArgument();
        this.output = buildOutputArgument();
    }

    public final void process(int seqid, TProtocol iprot, TProtocol oprot) throws TException {

        List<Object> values = null;

        try {
            values = input.read(iprot);
        } catch (TProtocolException var9) {
            iprot.readMessageEnd();
            TApplicationException x = new TApplicationException(7, var9.getMessage());
            oprot.writeMessageBegin(new TMessage(this.methodName, (byte)3, seqid));
            x.write(oprot);
            oprot.writeMessageEnd();
            oprot.getTransport().flush();
            return;
        }

        iprot.readMessageEnd();

        Object result;

        try {
            result = this.getResult(values);
        } catch (Exception var10) {
            log.error("Internal error processing " + this.methodName, var10);
            if(!this.isOneway()) {
                TApplicationException x = new TApplicationException(6, "Internal error processing " + this.methodName);
                oprot.writeMessageBegin(new TMessage(this.methodName, (byte)3, seqid));
                x.write(oprot);
                oprot.writeMessageEnd();
                oprot.getTransport().flush();
            }

            return;
        }

        if(!this.isOneway()) {
            oprot.writeMessageBegin(new TMessage(this.methodName, (byte)2, seqid));
            output.write(oprot, result);
            oprot.writeMessageEnd();
            oprot.getTransport().flush();
        }
    }

    protected boolean isOneway() {
        return false;
    }

    public Object getResult(List<Object> args) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(target, args);
    }

    private Method buildMethod() {

        Method[] methods = iface.getMethods();
        for(Method method : methods) {
            if(method.getName().equals(this.methodName)) {
               return method;
            }
        }

        return null;
    }

    private ServiceArgument buildInputArgument() {

        Class<?>[] types = this.method.getParameterTypes();
        return new ServiceArgument(types);
    }

    private ServiceResult buildOutputArgument() {

        return new ServiceResult(this.method.getReturnType());
    }
}
