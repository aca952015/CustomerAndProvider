package com.istudio.thrift.service;

import com.apache.thrift.annotation.TService;

/**
 * Created by ACA on 2017-5-26.
 */
@TService
public interface BlogService {

    UserProfile find(int id);
}
