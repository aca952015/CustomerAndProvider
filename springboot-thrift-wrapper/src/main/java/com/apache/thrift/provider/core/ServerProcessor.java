package com.apache.thrift.provider.core;

import com.apache.thrift.consumer.core.ServiceDefinition;
import com.apache.thrift.consumer.core.ServiceFunctionDefinition;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ACA on 2017/5/25.
 */
public class ServerProcessor implements org.apache.thrift.TProcessor {

    private final Class iface;
    private final Object target;
    private final ServiceDefinition def;
    private final Map<String, ServerFunction> processMap;

    public ServerProcessor(Class iface, Object target) {

        this.iface = iface;
        this.target = target;
        this.def = ServiceDefinition.getDef(iface);
        this.processMap = buildProcessMap();
    }

    private Map<String, ServerFunction> buildProcessMap() {

        Map<String, ServerFunction> map = new HashMap<>();
        for (ServiceFunctionDefinition funcDef : def.getFunctions().values()) {

            map.put(funcDef.getMethodName(), new ServerFunction(iface, target, funcDef));
        }

        return map;
    }

    @Override
    public boolean process(TProtocol in, TProtocol out) throws TException {
        TMessage msg = in.readMessageBegin();
        ServerFunction fn = this.processMap.get(msg.name);
        if (fn == null) {
            TProtocolUtil.skip(in, (byte) 12);
            in.readMessageEnd();
            TApplicationException x = new TApplicationException(1, "Invalid method name: '" + msg.name + "'");
            out.writeMessageBegin(new TMessage(msg.name, (byte) 3, msg.seqid));
            x.write(out);
            out.writeMessageEnd();
            out.getTransport().flush();
            return true;
        } else {
            fn.process(msg.seqid, in, out);
            return true;
        }
    }
}
