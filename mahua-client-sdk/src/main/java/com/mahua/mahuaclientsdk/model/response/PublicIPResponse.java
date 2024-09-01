package com.mahua.mahuaclientsdk.model.response;

import lombok.Data;

@Data
public class PublicIPResponse {

	String Country;
	// 省份
	String Province;
	// 城市
	String City;
	// 运营商
	String Isp;

	@Override
	public String toString() {
		return
				"国家：'" + Country + '\'' +
				"，省份：'" + Province + '\'' +
				"，城市：'" + City + '\'' +
				"，运营商：'" + Isp+ '\'';
	}
}
