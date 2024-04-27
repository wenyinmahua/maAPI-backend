package com.mahua.mahuaclientsdk.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class KeyPair {
	/**
	 * 公钥
	 */
	private String publicKey;
	/**
	 * 私钥
	 */
	private String privateKey;
}
