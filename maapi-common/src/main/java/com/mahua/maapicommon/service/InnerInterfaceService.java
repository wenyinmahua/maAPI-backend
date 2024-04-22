package com.mahua.maapicommon.service;

import com.mahua.maapicommon.model.entity.InterfaceInfo;

public interface InnerInterfaceService {

	/**
	 * 从数据库中查询接口是否存在（请求路径、请求方法）
	 * @param path
	 * @param method
	 * @return 接口信息，为空表示接口不存在
	 */
	InterfaceInfo getInterfaceInfo(String path, String method);
}
