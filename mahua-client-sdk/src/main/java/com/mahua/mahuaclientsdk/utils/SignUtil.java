package com.mahua.mahuaclientsdk.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * 签名工具
 */
public class SignUtil {
	/**
	 * 生成签名
	 *
	 * @param body
	 * @param secretKet
	 * @return
	 */
	public static String getSign(String body, String secretKet){
		Digester md5 = new Digester(DigestAlgorithm.SHA256);
		String content = body+"."+ secretKet;
		return md5.digestHex(content);
	}
}
