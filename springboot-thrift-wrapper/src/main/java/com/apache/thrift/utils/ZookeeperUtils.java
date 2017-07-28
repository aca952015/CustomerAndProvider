package com.apache.thrift.utils;

public class ZookeeperUtils {

    public static String getPath(String name, String version) {

        return name + "/v" + version;
    }
}
