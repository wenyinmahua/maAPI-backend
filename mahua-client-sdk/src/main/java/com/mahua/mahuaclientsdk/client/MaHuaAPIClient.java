package com.mahua.mahuaclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.mahua.mahuaclientsdk.model.User;

import java.util.HashMap;
import java.util.Map;

import static com.mahua.mahuaclientsdk.utils.SignUtil.getSign;

public class MaHuaAPIClient {

	private final String GATEWAY_URL = "localhost:8090";

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

		String result = HttpRequest.post(GATEWAY_URL + "/api/name/user")
				.body(body)
				.addHeaders(getHeaderMap(body))
				.execute().body();

		System.out.println(result);
		return result;
	}

	private Map<String, String> getHeaderMap(String body) {
		Map<String,String> headers = new HashMap<>();

		headers.put("accessKey",this.accessKey);
		// 一定不能发送
		// headers.put("secretKey",this.secretKet);
		headers.put("nonce", RandomUtil.randomNumbers(4));
		headers.put("body", body);
		headers.put("timestamp",String.valueOf(System.currentTimeMillis() / 1000));
		headers.put("sign",getSign(body,secretKet));
		return headers;
	}


}
