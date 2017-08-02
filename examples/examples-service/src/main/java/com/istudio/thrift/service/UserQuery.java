package com.istudio.thrift.service;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserQuery implements Serializable {

    private int id;
}
