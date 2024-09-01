package com.mahua.mahuaclientsdk.utils;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class KeyPair {
	/**
	 * 公钥
	 */
	private String accessKey;
	/**
	 * 私钥
	 */
	private String secretKet;
}
