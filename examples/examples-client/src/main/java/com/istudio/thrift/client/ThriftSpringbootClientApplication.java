package com.istudio.thrift.client;

import com.istudio.thrift.service.BlogService;
import com.istudio.thrift.service.CalcService;
import com.istudio.thrift.service.UserProfile;
import com.istudio.thrift.service.UserQuery;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Date;
import java.util.List;

@SpringBootApplication
public class ThriftSpringbootClientApplication {

	public static void main(String[] args) throws Exception {

		ConfigurableApplicationContext context = SpringApplication.run(ThriftSpringbootClientApplication.class, args);

		CalcService calcClient = context.getBean(CalcService.class);
		BlogService blogService = context.getBean(BlogService.class);

		System.out.println(calcClient.getClass());

		for(int loop = 1; true; loop++) {

			Date t1 = new Date();
			System.out.println("loop: " + loop);
			int called = 0;

			for(int pos = 0; pos < 500; pos++) {
				//calcClient.plus(123, 4556);
				UserQuery query = new UserQuery();
				query.setId(pos);

				List<UserProfile> profiles = blogService.query(query);
				System.out.println(profiles.get(0).getId());
				//System.out.println(profile.getBlogs().size());
			}

			Date t2 = new Date();

			System.out.println("loop NO." + loop + " elapsed time: " + (t2.getTime() - t1.getTime()) + "ms 500 invoked");

			Thread.sleep(1000L);
		}
	}
}
