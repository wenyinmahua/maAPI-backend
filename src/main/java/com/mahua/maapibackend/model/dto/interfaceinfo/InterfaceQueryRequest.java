package com.mahua.maapibackend.model.dto.interfaceinfo;

import com.mahua.maapibackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 *
 * @author mahua
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 创建人id
     */
    private Long uerId;

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