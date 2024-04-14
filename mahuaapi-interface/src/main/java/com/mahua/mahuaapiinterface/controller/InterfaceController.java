package com.mahua.mahuaapiinterface.controller;


import com.mahua.mahuaclientsdk.model.User;
import com.mahua.mahuaclientsdk.utils.SignUtil;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/name")
public class InterfaceController {

	@GetMapping("/")
	public String getNameByGet(String name){

		return "GET your name is" + name;
	}


	@PostMapping("/")
	public String getNameByPostURL(@RequestParam String name){
		return "POST your name is " + name;
	}


	@PostMapping("/user")
	public String getNameByPostJson(@RequestBody User user, HttpServletRequest request){
		String accessKey = request.getHeader("accessKey");
		String nonce = request.getHeader("nonce");
		String timestamp = request.getHeader("timestamp");
		String sign = request.getHeader("sign");
		String body = request.getHeader("body");
		// todo 实际情况是从数据库中查询是否已分配给用户
		if (!accessKey.equals("mahua")){
			throw new RuntimeException("无权限");
		}
		if(Long.valueOf(nonce) > 10000){
			throw new RuntimeException("无权限");
		}
		// 时间和当前时间不能超过5分钟
		long currentTimestamp = System.currentTimeMillis() /1000;
		if(currentTimestamp - Long.valueOf(timestamp) >= 5){
			throw new RuntimeException("无权限");
		}
		// todo 实际情况使从数据库中查出 secretKey
		String serverSign = SignUtil.getSign(body,"123456");
		if(!serverSign.equals(sign)){
			throw new RuntimeException("无权限");
		}
		return "POST JSON you name is "+ user.getName();
	}
}
