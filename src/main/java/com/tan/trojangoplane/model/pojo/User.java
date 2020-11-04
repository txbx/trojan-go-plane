package com.tan.trojangoplane.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : tan
 * @date: 2020/10/31 10:23
 * @description :
 */
@Data
@ApiModel("用户")
@TableName(value = "trojango.user")
public class User implements Serializable {

    @ApiModelProperty(name = "id", value = "主键", dataType = "Integer",hidden = true)
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    @ApiModelProperty(name = "username", value = "用户名/登录账号", dataType = "String")
    private String username;
    @ApiModelProperty(name = "pwhash", value = "用户密码加密后hash值", dataType = "String")
    private String pwhash;
    @ApiModelProperty(name = "password", value = "用户密码明文/连接trojango凭证", dataType = "String")
    private String password;
    @ApiModelProperty(name = "sumupload", value = "用户总上传流量（单位：字节）", dataType = "Long")
    private Long sumupload;
    @ApiModelProperty(name = "sumdownload", value = "用户总下载流量（单位：字节）", dataType = "Long")
    private Long sumdownload;
    @ApiModelProperty(name = "limitupload", value = "限制用户上传网速（单位，1字节/s）", dataType = "Long")
    private Long limitupload;
    @ApiModelProperty(name = "limitdownload", value = "限制用户下载网速（单位，1字节/s）", dataType = "Long")
    private Long limitdownload;
    @ApiModelProperty(name = "limitip", value = "限制用户同时在线ip数量", dataType = "Integer")
    private Integer limitip;
    @ApiModelProperty(name = "isintrojan", value = "是否能够进行trojan连接", dataType = "Integer")
    private Integer isintrojan;

    //补充字段
    @TableField(exist = false)
    @ApiModelProperty(name = "currentip", value = "同时在线ip", dataType = "Integer")
    private Integer currentip;
    @TableField(exist = false)
    @ApiModelProperty(name = "currentupload", value = "用户当前上传速度（单位，1字节/s）", dataType = "Long")
    private Long currentupload;
    @TableField(exist = false)
    @ApiModelProperty(name = "currentdownload", value = "用户当前下载度（（单位，1字节/s）", dataType = "Long")
    private Long currentdownload;
}