package com.apache.thrift.consumer.core;

import com.apache.thrift.common.ServiceArgument;
import com.apache.thrift.common.ServiceResult;
import org.apache.http.MethodNotSupportedException;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;
import org.springframework.beans.MethodInvocationException;

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

    protected void sendBase(String methodName, ServiceArgument args) throws TException {
        this.sendBase(methodName, args, (byte) 1);
    }

    protected void sendBaseOneway(String methodName, ServiceArgument args) throws TException {
        this.sendBase(methodName, args, (byte) 4);
    }

    private void sendBase(String methodName, Object[] args, byte type) throws TException {

        ServiceFunctionDefinition funcDef = def.getFunction(methodName);
        if(funcDef == null) {

            throw new TException("Method not found.");
        }

        this.oprot.writeMessageBegin(new TMessage(methodName, type, ++this.seqid));
        funcDef.getInput().write(this.oprot, args);
        this.oprot.writeMessageEnd();
        this.oprot.getTransport().flush();
    }

    protected void receiveBase(String methodName, ServiceResult result) throws TException {
        TMessage msg = this.iprot.readMessageBegin();
        if(msg.type == 3) {
            TApplicationException x = new TApplicationException();
            x.read(this.iprot);
            this.iprot.readMessageEnd();
            throw x;
        } else {
            System.out.format("Received %d%n", new Object[]{Integer.valueOf(msg.seqid)});
            if(msg.seqid != this.seqid) {
                throw new TApplicationException(4, String.format("%s failed: out of sequence response: expected %d but got %d", new Object[]{methodName, Integer.valueOf(this.seqid), Integer.valueOf(msg.seqid)}));
            } else {
                result.read(this.iprot);
                this.iprot.readMessageEnd();
            }
        }
    }
}
