package com.apache.thrift.utils;

public class EurekaUtils {

    public static String getPath(String name, String version) {

        return name + ".v" + version;
    }
}
