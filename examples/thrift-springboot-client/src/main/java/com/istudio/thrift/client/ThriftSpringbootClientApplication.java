package com.istudio.thrift.client;

import com.apache.thrift.common.ThriftProperties;
import com.istudio.thrift.service.CalcService;
import com.istudio.thrift.service.HelloService;
import org.apache.thrift.TException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Date;

@SpringBootApplication
public class ThriftSpringbootClientApplication {

	public static void main(String[] args) throws Exception {

		ConfigurableApplicationContext context = SpringApplication.run(ThriftSpringbootClientApplication.class, args);

		HelloService.Iface helloClient = context.getBean(HelloService.Iface.class);
		CalcService.Iface calcClient = context.getBean(CalcService.Iface.class);

		for(int loop = 1; true; loop++) {

			Date t1 = new Date();
			System.out.println("loop: " + loop);
			int called = 0;

			for(int pos = 0; pos < 100; pos++) {
				helloClient.sayBoolean(false);
				called++;
				helloClient.sayInt(123);
				called++;
				helloClient.sayString("hello");
				called++;
				helloClient.sayVoid();
				called++;

				calcClient.plus(123, 4556);
				called++;

				Thread.sleep(10L);
			}
			Date t2 = new Date();

			System.out.println("loop NO." + loop + " elapsed time: " + (t2.getTime() - t1.getTime()) + "ms " + called + " invoked");

			Thread.sleep(10000L);
		}
	}
}
