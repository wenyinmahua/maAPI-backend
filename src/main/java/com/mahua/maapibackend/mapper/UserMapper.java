package com.mahua.maapibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mahua.maapicommon.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
* @author mahua
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2024-04-09 22:48:38
* @Entity
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




