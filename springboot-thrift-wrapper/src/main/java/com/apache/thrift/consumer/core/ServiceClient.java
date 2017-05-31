package com.apache.thrift.consumer.core;

import com.apache.thrift.common.ServiceDefinition;
import com.apache.thrift.common.ServiceFunctionDefinition;
import lombok.Getter;
import lombok.Setter;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;

/**
 * Created by ACA on 2017/5/26.
 */
@Getter
@Setter
public class ServiceClient {
    protected final Class iface;
    protected final TProtocol prot;
    protected final ServiceDefinition def;
    protected int seqid;

    public ServiceClient(Class iface, TProtocol prot) {
        this.iface = iface;
        this.prot = prot;
        this.def = ServiceDefinition.getDef(iface);
    }

    public Object sendBase(String methodName, Object[] args) throws Exception {
        this.sendBase(methodName, args, (byte) 1);
        return receiveBase(methodName);
    }

    private void sendBase(String methodName, Object[] args, byte type) throws Exception {

        ServiceFunctionDefinition funcDef = def.getFunction(methodName);
        if (funcDef == null) {

            throw new TException("Method not found.");
        }

        this.prot.writeMessageBegin(new TMessage(this.iface.getName() + ":" + methodName, type, ++this.seqid));
        funcDef.getInput().write(this.prot, args);
        this.prot.writeMessageEnd();
        this.prot.getTransport().flush();
    }

    protected Object receiveBase(String methodName) throws TException {

        ServiceFunctionDefinition funcDef = def.getFunction(methodName);
        if (funcDef == null) {

            throw new TException("Method not found.");
        }

        TMessage msg = this.prot.readMessageBegin();
        if (msg.type == 3) {
            TApplicationException x = new TApplicationException();
            x.read(this.prot);
            this.prot.readMessageEnd();
            throw x;
        } else {
            if (msg.seqid != this.seqid) {
                throw new TApplicationException(4, String.format("%s failed: out of sequence response: expected %d but got %d", new Object[]{methodName, Integer.valueOf(this.seqid), Integer.valueOf(msg.seqid)}));
            } else {
                Object result = funcDef.getOutput().read(this.prot);
                this.prot.readMessageEnd();

                return result;
            }
        }
    }
}
