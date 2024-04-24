package com.mahua.maapibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mahua.maapicommon.model.entity.UserInterfaceInfo;

import java.util.List;

/**
* @author mahua
* @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Mapper
* @createDate 2024-04-16 15:23:24
* @Entity com.mahua.maapibackend.model.entity.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

	List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limitNums);
}




