package com.mahua.maapibackend.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mahua.maapibackend.common.ErrorCode;
import com.mahua.maapibackend.exception.BusinessException;
import com.mahua.maapibackend.mapper.InterfaceInfoMapper;
import com.mahua.maapicommon.model.entity.InterfaceInfo;
import com.mahua.maapicommon.service.InnerInterfaceService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerInterfaceServiceInfo implements InnerInterfaceService {
	@Resource
	InterfaceInfoMapper interfaceInfoMapper;
	@Override
	public InterfaceInfo getInterfaceInfo(String path, String method) {
		if (StringUtils.isAllEmpty(path,method)){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数不能为空");
		}
		QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("url",path);
		queryWrapper.eq("method",method);
		InterfaceInfo interfaceInfo = interfaceInfoMapper.selectOne(queryWrapper);
		if (interfaceInfo == null){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数错误");
		}
		return interfaceInfo;
	}
}
