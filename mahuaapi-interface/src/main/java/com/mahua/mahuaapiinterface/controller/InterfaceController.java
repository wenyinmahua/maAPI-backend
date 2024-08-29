package com.mahua.mahuaapiinterface.controller;


import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import com.mahua.mahuaapiinterface.service.PoemService;
import com.mahua.mahuaclientsdk.model.User;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.util.Base64;

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
		String url = "https://apis.juhe.cn/mobile/Example/query.php?phoneNo="+phoneNo;
		String result = HttpRequest.get(url).execute().body();
		JSONObject jsonResponse = new JSONObject(result);
		JSONObject resultObj = jsonResponse.getJSONObject("result");
		String province = resultObj.getStr("province");
		String city = resultObj.getStr("city");
		String company = resultObj.getStr("company");
		return province+city+company;
	}

	@GetMapping("/ip")
	public String getIPLocation(@RequestParam("ip") String ip){
		String url = "https://apis.juhe.cn/ip/Example/query.php?IP="+ip;
		String result = HttpRequest.get(url).execute().body();
		JSONObject jsonResponse = new JSONObject(result);
		JSONObject resultObj = jsonResponse.getJSONObject("result");
		String country = resultObj.getStr("Country");
		String province = resultObj.getStr("Province");
		String city = resultObj.getStr("City");
		String Isp = resultObj.getStr("Isp");
		return country+province+city+"<br/>"+" 供应商:"+Isp;
	}

//	https://api.btstu.cn/qrcode/api.php?text=aaaa&size=400

	@GetMapping("/qrcode")
	public String getQrcode(@RequestParam("text") String text,@RequestParam(value = "size",defaultValue = "400") String size){
		String url = " https://api.btstu.cn/qrcode/api.php?text="+text+"&size="+size;
		// 发送请求并获取响应体
		return HttpRequest.get(url).execute().body();
	}

}
