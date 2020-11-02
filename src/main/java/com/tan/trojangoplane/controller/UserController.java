package com.tan.trojangoplane.controller;

import com.tan.trojangoplane.model.pojo.Result;
import com.tan.trojangoplane.model.pojo.User;
import com.tan.trojangoplane.trojango.trojanUser;
import io.swagger.annotations.Api;
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

    @PostMapping("/add")
    @ApiOperation(value = "添加一个用户",notes = "添加一个用户")
    public Result add(@RequestBody User user){
        trojanuser.add(user);
        return new Result(true,"成功");
    }
}