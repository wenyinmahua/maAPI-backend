package com.mahua.maapibackend.model.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户调用接口关系表
 * @TableName user_interface_info
 */
@Data
public class UserInterfaceInfo implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 调用者id
     */
    private Long userId;

    /**
     * 接口id
     */
    private Long interfaceInfoId;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;

    /**
     * 0 - 正常 | 1 - 禁用
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}