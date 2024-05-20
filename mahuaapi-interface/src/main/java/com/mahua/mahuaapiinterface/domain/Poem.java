package com.mahua.mahuaapiinterface.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 古诗表
 * @TableName poem
 */
@TableName(value ="poem")
@Data
public class Poem implements Serializable {
    /**
     * 古诗id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 古诗标题
     */
    private String title;

    /**
     * 古诗朝代
     */
    private String dynasty;

    /**
     * 古诗作者
     */
    private String author;

    /**
     * 古诗内容
     */
    private String context;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}