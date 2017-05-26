package com.istudio.thrift.service;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ACA on 2017-5-26.
 */
@Getter
@Setter
public class UserProfile implements Serializable {

    private int id;
    private String name;
    private List<Blog> blogs;
}
