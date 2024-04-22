package com.mahua.maapicommon.service;

import com.mahua.maapicommon.model.entity.User;

public interface InnerUserService {
	/**
	 * 数据库中是否已经分配给用户密钥（accessKey、secretKey）
	 * @param accessKey
	 * @return 用户信息，为空表示用户不存在
	 */
	User getInvokeUser(String accessKey);
}
