package com.mahua.maapibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mahua.maapibackend.model.entity.Poem;
import com.mahua.maapibackend.service.PoemService;
import com.mahua.maapibackend.mapper.PoemMapping;
import org.springframework.stereotype.Service;

/**
* @author mahua
* @description 针对表【poem(古诗表)】的数据库操作Service实现
* @createDate 2024-05-08 13:30:50
*/
@Service
public class PoemServiceImpl extends ServiceImpl<PoemMapping, Poem>
    implements PoemService{

}




