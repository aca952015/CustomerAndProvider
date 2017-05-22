package com.istudio.thrift.client;

import com.istudio.thrift.service.HelloService;
import org.apache.thrift.TException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ThriftSpringbootClientApplication {

	public static void main(String[] args) throws TException {

		ConfigurableApplicationContext context = SpringApplication.run(ThriftSpringbootClientApplication.class, args);

		HelloService.Iface client = context.getBean(HelloService.Iface.class);
		client.sayBoolean(false);
		client.sayInt(123);
		client.sayString("hello");
		client.sayVoid();
	}
}
