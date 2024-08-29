package com.mahua.mahuaclientsdk.strategy.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import com.mahua.mahuaclientsdk.model.InterfaceInfoVO;
import com.mahua.mahuaclientsdk.strategy.InvokeStrategy;


import java.util.Map;

public class GetMethodStrategy implements InvokeStrategy {
	@Override
	public String strategy(String GateWayURL, InterfaceInfoVO interfaceInfoVO, Map<String, String> headerMap) {
		String requestParams = interfaceInfoVO.getRequestParams();
		JSONObject jsonObject = new JSONObject(requestParams);
		Iterable<String> keys = jsonObject.keySet();
		StringBuilder url = new StringBuilder();
		url.append(GateWayURL).append(interfaceInfoVO.getUrl()).append("?");
		for (String key : keys) {
			url.append(key).append("=").append(jsonObject.getStr(key));
		}
		return HttpRequest.get(url.toString())
				.addHeaders(headerMap)
				.execute().body();
	}

}
