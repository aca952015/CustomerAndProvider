package com.apache.thrift.consumer.core;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;

/**
 * Created by ACA on 2017/5/26.
 */
public class ServiceClient {
    protected final Class iface;
    protected final TProtocol iprot;
    protected final TProtocol oprot;
    protected final ServiceDefinition def;
    protected int seqid;

    public ServiceClient(Class iface, TProtocol prot) {
        this(iface, prot, prot);
    }

    public ServiceClient(Class iface, TProtocol iprot, TProtocol oprot) {
        this.iface = iface;
        this.iprot = iprot;
        this.oprot = oprot;
        this.def = ServiceDefinition.getDef(iface);
    }

    public Object sendBase(String methodName, Object[] args) throws Exception {
        this.sendBase(methodName, args, (byte) 1);
        return receiveBase(methodName);
    }

    public Object sendBaseOneway(String methodName, Object[] args) throws Exception {
        this.sendBase(methodName, args, (byte) 4);
        return receiveBase(methodName);
    }

    private void sendBase(String methodName, Object[] args, byte type) throws Exception {

        ServiceFunctionDefinition funcDef = def.getFunction(methodName);
        if (funcDef == null) {

            throw new TException("Method not found.");
        }

        this.oprot.writeMessageBegin(new TMessage(methodName, type, ++this.seqid));
        funcDef.getInput().write(this.oprot, args);
        this.oprot.writeMessageEnd();
        this.oprot.getTransport().flush();
    }

    protected Object receiveBase(String methodName) throws TException {

        ServiceFunctionDefinition funcDef = def.getFunction(methodName);
        if (funcDef == null) {

            throw new TException("Method not found.");
        }

        TMessage msg = this.iprot.readMessageBegin();
        if (msg.type == 3) {
            TApplicationException x = new TApplicationException();
            x.read(this.iprot);
            this.iprot.readMessageEnd();
            throw x;
        } else {
            if (msg.seqid != this.seqid) {
                throw new TApplicationException(4, String.format("%s failed: out of sequence response: expected %d but got %d", new Object[]{methodName, Integer.valueOf(this.seqid), Integer.valueOf(msg.seqid)}));
            } else {
                Object result = funcDef.getOutput().read(this.iprot);
                this.iprot.readMessageEnd();

                return result;
            }
        }
    }
}
