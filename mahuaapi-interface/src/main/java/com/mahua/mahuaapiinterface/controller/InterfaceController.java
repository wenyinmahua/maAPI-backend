package com.mahua.mahuaapiinterface.controller;


import com.mahua.mahuaclientsdk.model.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/name")
public class InterfaceController {

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
