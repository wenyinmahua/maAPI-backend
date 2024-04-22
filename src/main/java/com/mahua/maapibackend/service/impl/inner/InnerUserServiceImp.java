package com.mahua.maapibackend.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mahua.maapibackend.common.ErrorCode;
import com.mahua.maapibackend.exception.BusinessException;
import com.mahua.maapibackend.mapper.UserMapper;
import com.mahua.maapicommon.model.entity.User;
import com.mahua.maapicommon.service.InnerUserService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
@DubboService
public class InnerUserServiceImp implements InnerUserService {

	@Resource
	private UserMapper userMapper;
	@Override
	public User getInvokeUser(String accessKey) {
		if (accessKey == null){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数不能为空");
		}
		QueryWrapper<User> queryWrapper = new QueryWrapper();
		queryWrapper.eq("accessKey",accessKey);
		User user = userMapper.selectOne(queryWrapper);
		if (user == null){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户公钥错误");
		}
		return user;
	}
}
