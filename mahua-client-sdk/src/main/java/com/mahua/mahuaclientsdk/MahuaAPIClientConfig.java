package com.mahua.mahuaclientsdk;

import com.mahua.mahuaclientsdk.client.MaHuaAPIClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ComponentScan
@ConfigurationProperties("mahuaapi.client")
public class MahuaAPIClientConfig {

	private String accessKey;
	private String secretKey;

	@Bean
	public MaHuaAPIClient maHuaAPIClient(){
		return new MaHuaAPIClient(accessKey,secretKey);	}
}
