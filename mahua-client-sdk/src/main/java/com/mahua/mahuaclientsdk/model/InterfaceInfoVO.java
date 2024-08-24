package com.mahua.mahuaclientsdk.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 接口名称
 * @TableName interface_info
 */
@Data
public class InterfaceInfoVO implements Serializable {


    /**
     * 接口名称
     */
    private String name;

    /**
     * 接口描述
     */
    private String description;

    /**
     * 请求类型
     */
    private String url;

    /**
     * 地点
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