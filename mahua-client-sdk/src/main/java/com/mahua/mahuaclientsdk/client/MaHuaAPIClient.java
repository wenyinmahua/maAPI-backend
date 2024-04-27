package com.mahua.mahuaclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.mahua.mahuaclientsdk.model.User;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.mahua.mahuaclientsdk.utils.EncryptUtil.getSign;
@Slf4j
public class MaHuaAPIClient {

	private final String GATEWAY_URL = "http://localhost:8090";

	private String accessKey;
	private String secretKet;

	public MaHuaAPIClient(String accessKey, String secretKet) {
		this.accessKey = accessKey;
		this.secretKet = secretKet;
	}

	public String getNameByGet(String name){
		//可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
		HashMap<String, Object> paramMap = new HashMap<>();
		paramMap.put("name", name);
		String result= HttpUtil.get(GATEWAY_URL + "/api/name/get", paramMap);
		System.out.println(result);
		return result;
	}

	public String getNameByPostURL(String name){
		HashMap<String, Object> paramMap = new HashMap<>();
		paramMap.put("name", name);
		String result= HttpUtil.post(GATEWAY_URL + "/api/name/post", paramMap);
		System.out.println(result);
		return result;
	}

	public String getNameByPostJson(User user){
		String body = JSONUtil.toJsonStr(user);
		String url = GATEWAY_URL+"/api/name/user";
		log.info(url);
		String result = HttpRequest.post(url)
				.body(body)
				.addHeaders(getHeaderMap(body))
				.execute().body();

		System.out.println(result);
		return result;
	}

	private Map<String, String> getHeaderMap(String body) {
		Map<String,String> headers = new HashMap<>();

		headers.put("accessKey",this.accessKey);
		// 注意私钥一定不能发送，而是不同服务在后端中取出
		// headers.put("secretKey",this.secretKet);
		headers.put("nonce", RandomUtil.randomNumbers(4));
		headers.put("body",body);
//		try {
//			headers.put("body", URLEncoder.encode(body,"utf-8"));
//		} catch (UnsupportedEncodingException e) {
//			log.error("body准换失败");
//		}
		headers.put("timestamp",String.valueOf(System.currentTimeMillis() / 1000));
		// 使用私钥进行签名
		headers.put("sign", getSign(body,secretKet));
		return headers;
	}


}
