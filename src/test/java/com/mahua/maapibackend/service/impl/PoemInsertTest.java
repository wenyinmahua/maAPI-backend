package com.mahua.maapibackend.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;

import com.mahua.maapibackend.model.entity.Poem;
import com.mahua.maapibackend.service.PoemService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
@Slf4j
public class PoemInsertTest {

	@Resource
	private PoemService poemService;

	private ExecutorService executorService = new ThreadPoolExecutor(60,100,100, TimeUnit.SECONDS,new ArrayBlockingQueue<>(800000));

	/**
	 * 最简单的读
	 * <p>
	 * 1. 创建excel对应的实体对象 参照{@link Poem}
	 * <p>
	 * 3. 直接读即可
	 */
	@Test
	public void simpleRead() {

//		// 写法2：
//		// 匿名内部类 不用额外写一个DemoDataListener
		String fileName = "C:\\Users\\50184\\Desktop\\poem.xlsx";
		// 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		EasyExcel.read(fileName, Poem.class, new ReadListener<Poem>() {
			/**
			 * 单次缓存的数据量
			 */
			public static final int BATCH_COUNT = 100;
			/**
			 *临时存储
			 */
			private List<Poem> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

			@Override
			public void invoke(Poem data, AnalysisContext context) {
				cachedDataList.add(data);
				if (cachedDataList.size() >= BATCH_COUNT) {
					saveData();
					// 存储完成清理 list
					cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
				}
			}

			@Override
			public void doAfterAllAnalysed(AnalysisContext context) {
				saveData();
				stopWatch.stop();
				System.out.println(stopWatch.getTotalTimeMillis());
			}

			/**
			 * 加上存储数据库
			 */
			private void saveData() {
				for (Poem poem : cachedDataList){
					poemService.save(poem);
				}
			}
		}).sheet().doRead();

	}


	@Test
	public void doConcurrencyRead(){
		String fileName = "C:\\Users\\50184\\Desktop\\poem.xlsx";

		// 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		List<CompletableFuture<Void>> futureList = new ArrayList<>();
		try {
			EasyExcel.read(fileName, Poem.class, new ReadListener<Poem>() {
				public static final int BATCH_COUNT = 10;
				private List<Poem> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
				@Override
				public void invoke(Poem data, AnalysisContext context) {
					cachedDataList.add(data);
					if (cachedDataList.size() >= BATCH_COUNT) {
						CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
							poemService.saveBatch(cachedDataList,BATCH_COUNT);
							// 存储完成清理 list
							cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
						},executorService);
						futureList.add(future);

					}
				}
				@Override
				public void doAfterAllAnalysed(AnalysisContext context) {

				}

			}).sheet().doRead();
		} finally {
			CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
			stopWatch.stop();
			System.out.println(stopWatch.getTotalTimeMillis());
		}

	}
}
