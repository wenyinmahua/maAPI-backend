package com.mahua.mahuaclientsdk.client;

import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.mahua.mahuaclientsdk.model.InterfaceInfoVO;
import com.mahua.mahuaclientsdk.model.User;
import com.mahua.mahuaclientsdk.strategy.InvokeStrategy;
import com.mahua.mahuaclientsdk.strategy.impl.GetMethodStrategy;
import com.mahua.mahuaclientsdk.strategy.impl.POSTMethodStrategy;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.mahua.mahuaclientsdk.utils.EncryptUtils.getSign;
@Slf4j
public class MaHuaAPIClient {

	private final String GATEWAY_URL = "http://localhost:8090";

	private final String accessKey;
	private final String secretKet;

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
		headers.put("nonce", RandomUtil.randomNumbers(4));
		// 如果放入的值是中文的话,会出现中文编码错误,这里需要重新编码,下面的方法签名也需要编码后的数据,否则会验签错误.
		headers.put("body", URLEncodeUtil.encode(body, StandardCharsets.UTF_8));
		headers.put("timestamp",String.valueOf(System.currentTimeMillis() / 1000));
		// 使用私钥进行签名
		headers.put("sign", getSign(URLEncodeUtil.encode(body, StandardCharsets.UTF_8),secretKet));
		return headers;
	}

	public String invokeMethod(InterfaceInfoVO interfaceInfoVO){
		InvokeStrategy strategy = null;
		if (interfaceInfoVO.getMethod().equals("GET")){
			strategy = new GetMethodStrategy();
		}else if (interfaceInfoVO.getMethod().equals("POST")){
			strategy = new POSTMethodStrategy();
		}
		return strategy.strategy(GATEWAY_URL,interfaceInfoVO,getHeaderMap(interfaceInfoVO.getRequestParams()));
	}


}
