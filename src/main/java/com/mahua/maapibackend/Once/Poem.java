package com.mahua.maapibackend.Once;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class Poem {

	@ExcelProperty("标题")
	private String title;
	@ExcelProperty("朝代")
	private String dynasty;
	@ExcelProperty("作者")
	private String author;
	@ExcelProperty("内容")
	private String context;

	private Long id;
}
