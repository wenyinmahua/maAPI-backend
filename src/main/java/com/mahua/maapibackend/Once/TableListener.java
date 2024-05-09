package com.mahua.maapibackend.Once;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;

// 有个很重要的点 DemoDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
@Slf4j
public class TableListener implements ReadListener<Poem> {

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(Poem data, AnalysisContext context) {
        System.out.println(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        System.out.println("所有数据解析完成！");
    }

}