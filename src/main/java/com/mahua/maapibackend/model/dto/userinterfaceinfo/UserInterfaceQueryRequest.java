package com.mahua.maapibackend.model.dto.userinterfaceinfo;

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
public class UserInterfaceQueryRequest extends PageRequest implements Serializable {
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