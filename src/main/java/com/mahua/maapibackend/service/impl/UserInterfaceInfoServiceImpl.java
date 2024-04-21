package com.mahua.maapibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mahua.maapibackend.common.ErrorCode;
import com.mahua.maapibackend.exception.BusinessException;
import com.mahua.maapibackend.model.entity.UserInterfaceInfo;
import com.mahua.maapibackend.service.UserInterfaceInfoService;
import com.mahua.maapibackend.mapper.UserInterfaceInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author mahua
* @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Service实现
* @createDate 2024-04-16 15:23:24
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService {

	@Override
	public void validUserInterface(UserInterfaceInfo userInterfaceInfo, boolean add) {
		if (userInterfaceInfo == null){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		if (add){
			if (userInterfaceInfo.getInterfaceInfoId() <= 0 || userInterfaceInfo.getUserId() <= 0 ){
				throw new BusinessException(ErrorCode.PARAMS_ERROR);
			}
		}
		if (userInterfaceInfo.getLeftNum() < 0){
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "剩余次数不能小于0");
		}
	}

	@Override
	public boolean invokeCount(long interfaceInfoId, long userId) {
		if (interfaceInfoId <= 0 || userId <= 0){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
		updateWrapper.set("interfaceInfoId",interfaceInfoId);
		updateWrapper.set("userId",userId);
		updateWrapper.gt("leftNum",0);
		updateWrapper.setSql("leftNum = leftNum - 1, totalNum = totalNum + 1");
		return this.update(updateWrapper);
	}
}




