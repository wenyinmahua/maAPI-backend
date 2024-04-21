package com.mahua.maapibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mahua.maapibackend.common.ErrorCode;
import com.mahua.maapibackend.exception.BusinessException;
import com.mahua.maapibackend.model.entity.InterfaceInfo;
import com.mahua.maapibackend.service.InterfaceInfoService;
import com.mahua.maapibackend.mapper.InterfaceInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author mahua
* @description 针对表【interface_info(接口名称)】的数据库操作Service实现
* @createDate 2024-04-09 22:22:58
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService{
	@Override
	public void validInterface(InterfaceInfo interfaceInfo, boolean add) {
		if (interfaceInfo == null){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		String name = interfaceInfo.getName();
		if (add){
			if (StringUtils.isAnyBlank(name)){
				throw new BusinessException(ErrorCode.PARAMS_ERROR);
			}
		}
		if (StringUtils.isNotBlank(name) && name.length() >= 50){
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求名称过长");
		}
	}
}




