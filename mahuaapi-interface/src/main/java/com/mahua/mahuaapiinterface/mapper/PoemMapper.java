package com.mahua.mahuaapiinterface.mapper;

import com.mahua.mahuaapiinterface.domain.Poem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author mahua
* @description 针对表【poem(古诗表)】的数据库操作Mapper
* @createDate 2024-05-16 08:13:45
* @Entity generator.domain.Poem
*/
public interface PoemMapper extends BaseMapper<Poem> {

	Poem getRandomPoem();
}




