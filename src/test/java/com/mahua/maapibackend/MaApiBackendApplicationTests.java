package com.mahua.maapibackend;

import com.mahua.maapibackend.utils.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class MaApiBackendApplicationTests {

	@Resource
	private MailService mailService;

	@Test
	void sendEMail() {
		mailService.sendTextMailMessage("zhangshuai.io@foxmail.com","你好","你好，张帅同学，爽不爽？");
	}
	//那怎么办，没啥扩展的

}
