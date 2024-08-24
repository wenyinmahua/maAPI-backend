package com.mahua.mahuaapiinterface.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mahua.mahuaapiinterface.domain.Poem;

/**
* @author mahua
* @description 针对表【poem(古诗表)】的数据库操作Service
* @createDate 2024-08-24 17:16:19
*/
public interface PoemService extends IService<Poem> {

	String getRandomPoem();
}
