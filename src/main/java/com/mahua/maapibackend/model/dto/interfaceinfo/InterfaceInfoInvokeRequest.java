package com.mahua.maapibackend.model.dto.interfaceinfo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 接口调用请求
 *
 */
@TableName(value ="interface_info")
@Data
public class InterfaceInfoInvokeRequest implements Serializable {
    /**
     * id
     */
    private Long id;


    /**
     * 请求参数
     * [
     *   {
     *     "name": "username",
     *     "type": "string"
     *   }
     * ]
     */
    private String userRequestParams;

    private static final long serialVersionUID = 1L;
}