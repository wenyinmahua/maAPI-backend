package com.mahua.maapibackend.service;

import com.mahua.maapibackend.model.entity.UserInterfaceInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author mahua
* @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Service
* @createDate 2024-04-16 15:23:24
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

	void validUserInterface(UserInterfaceInfo interfaceInfo, boolean add);

	boolean invokeCount(long interfaceInfoId,long userId);
}
