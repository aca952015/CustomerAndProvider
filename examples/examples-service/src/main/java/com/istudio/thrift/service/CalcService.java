package com.istudio.thrift.service;

import com.apache.thrift.annotation.TService;

@TService
public interface CalcService {

  int plus(int param1, int param2);
}
