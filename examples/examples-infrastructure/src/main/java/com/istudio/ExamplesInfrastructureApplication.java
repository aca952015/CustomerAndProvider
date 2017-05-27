package com.istudio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ExamplesInfrastructureApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExamplesInfrastructureApplication.class, args);
	}
}
