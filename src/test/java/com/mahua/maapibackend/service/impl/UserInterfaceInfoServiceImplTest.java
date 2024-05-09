package com.mahua.maapibackend.service.impl;



import com.mahua.maapicommon.service.InnerInterfaceService;
import com.mahua.maapicommon.service.UserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


@SpringBootTest
public class UserInterfaceInfoServiceImplTest {

	@DubboReference
	private InnerInterfaceService innerInterfaceService;

	@Resource
	private UserInterfaceInfoService userInterfaceInfoService;

	@Test
	public void invokeCount() {
		boolean b = userInterfaceInfoService.invokeCount(1L, 1L);
		Assertions.assertEquals(false,b);
	}

	@Test
	void getInterfaceInfo(){
		String url = "/api/name/get";
		String method = "POST";
		innerInterfaceService.getInterfaceInfo(url,method);
	}
}