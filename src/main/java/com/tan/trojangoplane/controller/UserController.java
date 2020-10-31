package com.tan.trojangoplane.controller;

import com.tan.trojangoplane.model.pojo.Result;
import com.tan.trojangoplane.model.pojo.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : tan
 * @date: 2020/10/31 11:18
 * @description :
 */
@RestController
@Api(tags = "用户管理")
@RequestMapping("/user")
public class UserController {

    @PostMapping("/findAll")
    @ApiOperation(value = "获取所有用户",notes = "获取所有用户")
    public Result findAll(@RequestBody User user){
        Integer id = user.getId();
        return new Result(true,"成功",id);
    }
}