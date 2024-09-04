package com.mahua.mahuaapiinterface.config;

import com.mahua.mahuaapiinterface.interceptor.GatewayInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebConfig implements WebMvcConfigurer {


	@Resource
	private GatewayInterceptor gatewayInterceptor;
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(gatewayInterceptor)
				.addPathPatterns("/**");
	}
}
