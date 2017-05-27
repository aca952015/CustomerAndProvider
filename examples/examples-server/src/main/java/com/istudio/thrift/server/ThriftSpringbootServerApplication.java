package com.istudio.thrift.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ThriftSpringbootServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThriftSpringbootServerApplication.class, args);
	}
}
