package com.mahua.mahuaclientsdk.strategy.impl;

import cn.hutool.http.HttpRequest;
import com.mahua.mahuaclientsdk.model.InterfaceInfoVO;
import com.mahua.mahuaclientsdk.strategy.InvokeStrategy;


import java.util.Map;

public class GetMethodStrategy implements InvokeStrategy {
	@Override
	public String strategy(String GateWayURL, InterfaceInfoVO interfaceInfoVO, Map<String, String> headerMap) {
		return HttpRequest.get(GateWayURL + interfaceInfoVO.getUrl())
				.body(interfaceInfoVO.getRequestParams())
				.addHeaders(headerMap)
				.execute().body();
	}

}
