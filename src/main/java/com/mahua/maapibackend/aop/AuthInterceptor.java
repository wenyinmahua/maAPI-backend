package com.mahua.maapibackend.aop;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.mahua.maapibackend.annotation.AuthCheck;
import com.mahua.maapibackend.common.ErrorCode;
import com.mahua.maapibackend.exception.BusinessException;
import com.mahua.maapibackend.model.entity.User;
import com.mahua.maapibackend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Aspect
public class AuthInterceptor {

	@Resource
	private UserService userService;

	@Around("@annotation(authCheck)")
	public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
		List<String> anyRole = Arrays.stream(authCheck.anyRole()).filter(StringUtils::isNotBlank).collect(Collectors.toList());
		String mustRole = authCheck.mustRole();
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();

		//当前登录的用户
		User user = userService.getLoginUser(request);
		String userRole = user.getUserRole();
		if(CollectionUtils.isNotEmpty(anyRole)){
			if (!anyRole.contains(userRole)){
				throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
			}
		}
		if (StringUtils.isNotBlank(mustRole)){
			if (!mustRole.equals(userRole)){
				throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
			}
		}
		//通过权限校验，放行
		return joinPoint.proceed();
	}
}
