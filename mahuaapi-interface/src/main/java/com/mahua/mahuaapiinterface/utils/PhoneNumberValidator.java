package com.mahua.mahuaapiinterface.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumberValidator {
	/**
	 * 验证电话号码格式的方法
	 *
	 * @param phoneNumber 输入的电话号码
	 * @return 如果电话号码格式正确返回 true，否则返回 false
	 */
	public static boolean isValidPhoneNumber(String phoneNumber) {
		// 定义电话号码的正则表达式
		// 示例正则表达式：支持以下几种格式
		// 1. 国际区号 + 区号 + 号码 (例如：+86 13800138000)
		// 2. 直接输入号码 (例如：13800138000)
		// 3. 带区号的本地号码 (例如：010-12345678)
		String regex = "^\\+?\\d{1,3}?[-. ]?\\(?(?:\\d{2,3})\\)?[-. ]?(?:\\d{3,4})[-. ]?(?:\\d{4})$|^\\d{10,11}$";

		// 创建 Pattern 对象
		Pattern pattern = Pattern.compile(regex);

		// 创建 Matcher 对象
		Matcher matcher = pattern.matcher(phoneNumber);

		// 检查是否匹配
		return matcher.matches();
	}

}
