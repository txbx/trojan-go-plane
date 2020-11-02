package com.tan.trojangoplane.model.pojo;

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
public class User implements Serializable {

    @ApiModelProperty(name = "id", value = "主键", dataType = "Integer",hidden = true)
    private Integer id;
    @ApiModelProperty(name = "pwhash", value = "用户密码加密后hash值", dataType = "String")
    private String pwhash;
    @ApiModelProperty(name = "password", value = "用户密码明文", dataType = "String")
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

    //补充字段
    @ApiModelProperty(name = "currentupload", value = "用户当前上传速度（单位，1字节/s）", dataType = "Long")
    private Long currentupload;
    @ApiModelProperty(name = "currentdownload", value = "用户当前下载度（（单位，1字节/s）", dataType = "Long")
    private Long currentdownload;
}