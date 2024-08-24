package com.mahua.mahuaapiinterface.mapper;

import com.mahua.mahuaapiinterface.domain.Poem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author mahua
* @description 针对表【poem(古诗表)】的数据库操作Mapper
* @createDate 2024-08-24 17:16:18
* @Entity generator.domain.Poem
*/
@Mapper
public interface PoemMapper extends BaseMapper<Poem> {

	Poem getRandomPoem();
}




