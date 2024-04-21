package com.mahua.maapibackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * Knife4j 配置
 *
 * @author mahua
 */
@Configuration
@EnableSwagger2WebMvc
@Profile({"dev"})
public class Knife4jConfiguration {

	@Bean(value = "defaultApi2")
	public Docket defaultApi2() {
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(new ApiInfoBuilder()
						.title("麻花API中心")
						.description("# 麻花API中心")
						.termsOfServiceUrl("http://www.github.com/wenyinmahua")
						.contact(new Contact("mahua","http://www.github.com/wenyinmahua","501847822@qq.com"))
						.version("1.0")
						.build())
				//分组名称
				.groupName("1.0版本")
				.select()
				//这里指定Controller扫描包路径
				.apis(RequestHandlerSelectors.basePackage("com.mahua.maapibackend.controller"))
				.paths(PathSelectors.any())
				.build();
	}
}