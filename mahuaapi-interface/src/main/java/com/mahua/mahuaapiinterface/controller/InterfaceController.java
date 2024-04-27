package com.mahua.mahuaapiinterface.controller;


import com.mahua.mahuaclientsdk.model.User;
import com.mahua.mahuaclientsdk.utils.SignUtil;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/name")
public class InterfaceController {

	private final Long FIVE_MINUTE=60*5l;

	@GetMapping("/get")
	public String getNameByGet(String name,HttpServletRequest request){
		return "GET your name is " + name;
	}


	@PostMapping("/post")
	public String getNameByPostURL(@RequestParam String name) {
		return "POST your name is " + name;
	}


	@PostMapping("/user")
	public String getNameByPostJson(@RequestBody User user, HttpServletRequest request){

		return "POST JSON you name is "+ user.getName();
	}
}
