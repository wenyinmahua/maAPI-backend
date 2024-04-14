package com.mahua.maapibackend.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新请求
 *
 * @TableName product
 */
@Data
public class InterfaceOnlineOrOfflineRequest implements Serializable {
    /**
     * id
     */
    private Long id;


    private static final long serialVersionUID = 1L;
}