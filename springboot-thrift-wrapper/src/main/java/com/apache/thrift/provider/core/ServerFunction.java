package com.apache.thrift.provider.core;

import com.apache.thrift.common.ServiceFunctionDefinition;
import lombok.extern.log4j.Log4j;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolException;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by ACA on 2017/5/25.
 */
@Log4j
public class ServerFunction {

    private final Class iface;
    private final Object target;
    private final ServiceFunctionDefinition def;

    public ServerFunction(Class iface, Object target, ServiceFunctionDefinition def) {

        this.iface = iface;
        this.target = target;
        this.def = def;
    }

    public final void process(int seqid, TProtocol iprot, TProtocol oprot) throws TException {

        Object[] values = null;

        try {
            values = def.getInput().read(iprot);

        } catch (TProtocolException var9) {

            iprot.readMessageEnd();
            TApplicationException x = new TApplicationException(7, var9.getMessage());
            oprot.writeMessageBegin(new TMessage(this.def.getMethodName(), (byte)3, seqid));
            x.write(oprot);
            oprot.writeMessageEnd();
            oprot.getTransport().flush();
            return;
        }

        iprot.readMessageEnd();

        Object result;

        try {
            result = this.getResult(values);

        } catch (Exception e) {

            log.error("Internal error processing " + this.def.getMethodName(), e);

            if(!this.isOneway()) {

                TApplicationException x = new TApplicationException(6, e.getCause().getMessage());
                oprot.writeMessageBegin(new TMessage(this.def.getMethodName(), (byte)3, seqid));
                x.write(oprot);
                oprot.writeMessageEnd();
                oprot.getTransport().flush();
            }

            return;
        }

        if(!this.isOneway()) {

            oprot.writeMessageBegin(new TMessage(this.def.getMethodName(), (byte)2, seqid));
            def.getOutput().write(oprot, result);
            oprot.writeMessageEnd();
            oprot.getTransport().flush();
        }
    }

    protected boolean isOneway() {

        return false;
    }

    public Object getResult(Object[] args) throws InvocationTargetException, IllegalAccessException {

        return def.getMethod().invoke(target, args);
    }
}
