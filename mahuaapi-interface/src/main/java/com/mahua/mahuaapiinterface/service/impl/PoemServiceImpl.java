package com.mahua.mahuaapiinterface.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.mahua.mahuaapiinterface.domain.Poem;
import com.mahua.mahuaapiinterface.mapper.PoemMapper;
import com.mahua.mahuaapiinterface.service.PoemService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author mahua
* @description 针对表【poem(古诗表)】的数据库操作Service实现
* @createDate 2024-08-24 17:16:19
*/
@Service
public class PoemServiceImpl extends ServiceImpl<PoemMapper, Poem>
    implements PoemService {

	@Resource
	private PoemMapper poemMapper;

	@Override
	public String getRandomPoem() {
		Poem poem = poemMapper.getRandomPoem();
		while (poem == null){
			poem = poemMapper.getRandomPoem();
		}
		Gson gson = new Gson();
		String poemString = gson.toJson(poem);
		return poemString;
	}
}




