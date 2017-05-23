package com.istudio.thrift.server.impl;

import com.istudio.thrift.service.HelloService;
import lombok.extern.log4j.Log4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Component;

/**
 * Created by ACA on 2017/5/22.
 */
@Log4j
@Component
public class HelloServiceImpl implements HelloService.Iface {
    @Override
    public int sayInt(int param) throws TException {

        log.info("from client: sayInt " + param);

        return 0;
    }

    @Override
    public String sayString(String param) throws TException {

        log.info("from client: sayString " + param);

        return param;
    }

    @Override
    public boolean sayBoolean(boolean param) throws TException {

        log.info("from client: sayBoolean " + param);

        return param;
    }

    @Override
    public void sayVoid() throws TException {

        log.info("from client: sayVoid");
    }
}
