package com.istudio.thrift.server.impl;

import com.istudio.thrift.service.CalcService;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

/**
 * Created by ACA on 2017/5/23.
 */
@Component
@Log4j
public class CalcServiceImpl implements CalcService {

    @Override
    public int plus(int param1, int param2) {

        log.info("from client: plus " + param1 + " + " + param2);

        return param1 + param2;
    }
}
