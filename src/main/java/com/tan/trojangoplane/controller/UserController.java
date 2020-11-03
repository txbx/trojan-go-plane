package com.tan.trojangoplane.controller;

import com.tan.trojangoplane.model.pojo.Result;
import com.tan.trojangoplane.model.pojo.User;
import com.tan.trojangoplane.trojango.callback.CallBack;
import com.tan.trojangoplane.trojango.trojanUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : tan
 * @date: 2020/10/31 11:18
 * @description :
 */
@RestController
@Api(tags = "用户管理")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private trojanUser trojanuser;


    @PostMapping("/findAll")
    @ApiOperation(value = "获取所有用户",notes = "获取所有用户")
    public Result findAll(){
        List<User> trojanuserAll = trojanuser.findAll();
        return new Result(true,"成功",trojanuserAll);
    }

    @PostMapping("/findByPw")
    @ApiOperation(value = "根据密码查找用户",notes = "根据密码查找用户")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name="pw", dataType="String", required=true, value="密码")
    })
    public Result findByPw(String pw){
        CallBack callBack = new CallBack();
        return trojanuser.findbypw(callBack, pw);
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加一个用户",notes = "添加一个用户")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name="pw", dataType="String", required=true, value="用户密码"),
            @ApiImplicitParam(paramType="query", name="limitip", dataType="Integer", value="限制同时在线ip数"),
            @ApiImplicitParam(paramType="query", name="limitDownload", dataType="Long", value="限制用户下载速度：字节/秒",example = "1048576"),
            @ApiImplicitParam(paramType="query", name="limitUpload", dataType="Long", value="限制用户上传速度：字节/秒",example = "1048576")
    })
    public Result add(String pw,Integer limitip,Long limitDownload,Long limitUpload){
        User user = new User();
        user.setPassword(pw);
        user.setLimitip(limitip);
        user.setLimitdownload(limitDownload);
        user.setLimitupload(limitUpload);
        CallBack callBack = new CallBack();
        return trojanuser.operationTrojanUser(callBack,user,0);
    }

    @PostMapping("/deleteUserByhash")
    @ApiOperation(value = "删除用户",notes = "删除用户")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name="pw", dataType="String", required=true, value="1")
    })
    public Result deleteUserByhash(String pw){
        User user = new User();
        user.setPassword(pw);
        CallBack callBack = new CallBack();
        //返回结果才return
        return trojanuser.operationTrojanUser(callBack,user,1);
    }

    @PostMapping("/updateUser")
    @ApiOperation(value = "根据密码修改用户",notes = "根据密码修改用户")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name="pw", dataType="String", required=true, value="用户密码"),
            @ApiImplicitParam(paramType="query", name="limitip", dataType="Integer", value="限制同时在线ip数"),
            @ApiImplicitParam(paramType="query", name="limitDownload", dataType="Long", value="限制用户下载速度：字节/秒",example = "1048576"),
            @ApiImplicitParam(paramType="query", name="limitUpload", dataType="Long", value="限制用户上传速度：字节/秒",example = "1048576")
    })
    public Result updateUser(String pw,Integer limitip,Long limitDownload,Long limitUpload){
        User user = new User();
        user.setPassword(pw);
        user.setLimitip(limitip);
        user.setLimitdownload(limitDownload);
        user.setLimitupload(limitUpload);
        CallBack callBack = new CallBack();
        return trojanuser.operationTrojanUser(callBack, user, 2);
    }

    //总流量清零


}