package com.mahua.maapibackend.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 更新请求
 *
 * @TableName product
 */
@Data
public class InterfaceUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;


    /**
     * 接口名称
     */
    private String name;

    /**
     * 接口描述
     */
    private String description;

    /**
     * 请求地址
     */
    private String url;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 接口状态（0 - 关闭 1 - 开启）
     */
    private Integer status;


    private static final long serialVersionUID = 1L;
}