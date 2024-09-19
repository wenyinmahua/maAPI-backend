package com.mahua.mahuaclientsdk.model.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WeiboHotResponse {
	private String link;
	private String data;
//	private List<WeiboHot> weiboHots = new ArrayList<>();

	@Override
	public String toString() {
		return "WeiboHotResponse{" +
				"link='" + link + '\'' +
				"\n data='" + data + '\'' +
				'}';
	}
}
@Data
class WeiboHot{
	private String title;
	private int hot;
	private String url;
}
