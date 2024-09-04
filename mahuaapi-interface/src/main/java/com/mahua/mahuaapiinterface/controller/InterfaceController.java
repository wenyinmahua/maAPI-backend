package com.mahua.mahuaapiinterface.controller;


import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import com.google.gson.Gson;
import com.mahua.mahuaapiinterface.service.PoemService;
import com.mahua.mahuaapiinterface.utils.PhoneNumberValidator;
import com.mahua.mahuaclientsdk.model.User;
import com.mahua.mahuaclientsdk.model.response.PhoneNumberLocationResponse;
import com.mahua.mahuaclientsdk.model.response.PublicIPResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;

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

	@GetMapping("/phone")
	public String getPhoneLocation(@PathParam("phoneNo") String phoneNo){
		if (!PhoneNumberValidator.isValidPhoneNumber(phoneNo)){
			return "请保证你输入的电话号码合法";
		}
		String url = "https://apis.juhe.cn/mobile/Example/query.php?phoneNo="+phoneNo;
		String response = HttpRequest.get(url).execute().body();
		String result = new JSONObject(response).getStr("result");
		if (result == null){
			return "手机号格式不正确";
		}
		Gson gson = new Gson();
		PhoneNumberLocationResponse phoneNumberLocationResponse = gson.fromJson(result, PhoneNumberLocationResponse.class);
		return phoneNumberLocationResponse.toString();
	}

	@GetMapping("/ip")
	public String getIPLocation(@RequestParam("ip") String ip){
		String url = "https://apis.juhe.cn/ip/Example/query.php?IP="+ip;
		String resopnse = HttpRequest.get(url).execute().body();
		JSONObject jsonResponse = new JSONObject(resopnse);
		String result = jsonResponse.getStr("result");
		if (result == null){
			return "IP 地址错误";
		}
		Gson gson = new Gson();
		PublicIPResponse publicIPResponse = gson.fromJson(result, PublicIPResponse.class);
		return publicIPResponse.toString();
	}


	@GetMapping("/qrcode")
	public String getQrcode(@RequestParam("text") String text,@RequestParam(value = "size",defaultValue = "400") String size){
		String url = " https://api.btstu.cn/qrcode/api.php?text="+text+"&size="+size;
		// 发送请求并获取响应体
		return HttpRequest.get(url).execute().body();
	}

	@GetMapping("/weather")
	public String getWeather(){
		String url = "https://api.vvhan.com/api/weather";
		String resopne = HttpRequest.get(url).execute().body();
		return resopne;
	}


}
