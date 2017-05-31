package com.apache.thrift.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.map.annotate.JsonRootName;

/**
 * Created by ACA on 2017-5-28.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonRootName("serviceInfo")
public class ServiceInfo {

    private String id;
    private String name;
    private String group;
    private String version;
}
