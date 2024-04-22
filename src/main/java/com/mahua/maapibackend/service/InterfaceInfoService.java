package com.mahua.maapibackend.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.mahua.maapicommon.model.entity.InterfaceInfo;

/**
* @author mahua
* @description 针对表【interface_info(接口名称)】的数据库操作Service
* @createDate 2024-04-09 22:22:58
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

	void validInterface(InterfaceInfo interfaceInfo, boolean add);

}
