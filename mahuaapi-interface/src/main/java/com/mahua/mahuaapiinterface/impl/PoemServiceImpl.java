package com.mahua.mahuaapiinterface.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mahua.mahuaapiinterface.service.PoemService;
import com.mahua.mahuaapiinterface.domain.Poem;
import com.mahua.mahuaapiinterface.mapper.PoemMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author mahua
* @description 针对表【poem(古诗表)】的数据库操作Service实现
* @createDate 2024-05-16 08:13:45
*/
@Service
public class PoemServiceImpl extends ServiceImpl<PoemMapper, Poem>
    implements PoemService {

	@Resource
	private PoemMapper poemMapper;
	@Override
	public Poem getRandomPoem() {
		Poem randomPoem = poemMapper.getRandomPoem();
		while (randomPoem == null){
			randomPoem = poemMapper.getRandomPoem();
		}
		return randomPoem;
	}
}




