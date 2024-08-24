package com.mahua.mahuaclientsdk.strategy;

import com.mahua.mahuaclientsdk.model.InterfaceInfoVO;

import java.util.Map;

public interface InvokeStrategy {
	public String strategy(String GateWayURL, InterfaceInfoVO interfaceInfoVO, Map<String, String> headerMap);
}
