package com.istudio.thrift.service;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by ACA on 2017-5-26.
 */
@Getter
@Setter
public class Blog implements Serializable {

    private String title;
    private String content;
    private String tags;
}
