package com.mahua.maapibackend;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubbo
@SpringBootApplication
public class MaApiBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaApiBackendApplication.class, args);
	}

}
