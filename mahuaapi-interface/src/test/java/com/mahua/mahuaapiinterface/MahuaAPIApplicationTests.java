package com.mahua.mahuaapiinterface;


import com.mahua.mahuaclientsdk.client.MaHuaAPIClient;
import com.mahua.mahuaclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class MahuaAPIApplicationTests {
	@Resource
	private MaHuaAPIClient maHuaAPIClient;

	@Test
	void contextLoads(){
		String mahua = maHuaAPIClient.getNameByGet("mahua");
		User user = new User();
		user.setName("mahua");
		String userNameGetByPost = maHuaAPIClient.getNameByPostJson(user);
		System.out.println(mahua);
		System.out.println(userNameGetByPost);
	}
}
