package com.mahua.mahuaapiinterface.controller;


import cn.hutool.http.HttpRequest;
import com.mahua.mahuaapiinterface.service.PoemService;
import com.mahua.mahuaclientsdk.model.User;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/service")
public class InterfaceController {

	@Resource
	private PoemService poemService;

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

	@GetMapping("/poem")
	public String getRandomPoem(){
		return poemService.getRandomPoem();
	}

	@GetMapping("/jitang")
	public String getRandmo(){
		String url = "https://api.btstu.cn/yan/api.php";
		return HttpRequest.get(url).execute().body();
	}
}
