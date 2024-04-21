package com.mahua.maapigateway;

import com.mahua.maapibackend.provider.DemoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@EnableDubbo
@SpringBootApplication
public class MaapiGatewayApplication {

	@DubboReference
	private DemoService demoService;

	public static void main(String[] args) {

		ConfigurableApplicationContext context = SpringApplication.run(MaapiGatewayApplication.class, args);
		MaapiGatewayApplication application = context.getBean(MaapiGatewayApplication.class);
		String result = application.doSayHello("world");
		String result2 = application.doSayHello2("world");
		System.out.println("result: " + result);
		System.out.println("result: " + result2);

	}

	public String doSayHello(String name){
		return demoService.sayHello(name);
	}
	public String doSayHello2(String name){
		return demoService.sayHello2(name);
	}
}
