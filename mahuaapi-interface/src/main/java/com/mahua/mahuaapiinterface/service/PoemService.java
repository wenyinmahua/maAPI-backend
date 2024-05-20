package com.mahua.mahuaapiinterface.service;

import com.mahua.mahuaapiinterface.domain.Poem;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author mahua
* @description 针对表【poem(古诗表)】的数据库操作Service
* @createDate 2024-05-16 08:13:45
*/
public interface PoemService extends IService<Poem> {

	Poem getRandomPoem();
}
