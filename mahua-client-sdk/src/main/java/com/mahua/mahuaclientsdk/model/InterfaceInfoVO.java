package com.mahua.mahuaclientsdk.model;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 接口
 */
@Data
public class InterfaceInfoVO implements Serializable {

    /**
     * 请求 URL
     */
    private String url;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 请求参数
     * [
     *   {
     *     "name": "username",
     *     "type": "string"
     *   }
     * ]
     */
    private String requestParams;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}