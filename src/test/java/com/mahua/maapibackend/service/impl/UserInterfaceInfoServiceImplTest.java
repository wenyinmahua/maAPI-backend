package com.mahua.maapibackend.service.impl;

import com.mahua.maapibackend.service.UserInterfaceInfoService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


@SpringBootTest
public class UserInterfaceInfoServiceImplTest {

	@Resource
	private UserInterfaceInfoService userInterfaceInfoService;

	@Test
	public void invokeCount() {
		boolean b = userInterfaceInfoService.invokeCount(1L, 1L);
		Assertions.assertEquals(false,b);
	}
}