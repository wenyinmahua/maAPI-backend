package com.mahua.mahuaclientsdk.model.response;

import lombok.Data;

@Data
public class PhoneNumberLocationResponse {

	private String province;
	private String city;
	private String company;
	private String areacode;

	@Override
	public String toString() {
		return
				"省份：\"" + province + '\"' +
				"，城市：\"" + city + '\"' +
				"，运营商：\"" + company + '\"' +
				"，区号：\"" + areacode + '\"';
	}
}
