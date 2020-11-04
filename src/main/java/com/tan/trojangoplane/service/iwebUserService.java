package com.tan.trojangoplane.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tan.trojangoplane.model.pojo.Result;
import com.tan.trojangoplane.model.pojo.User;

import java.util.List;

/**
 * @author : tan
 * @date: 2020/11/4 11:55
 * @description :用户增删改查
 */
public interface iwebUserService extends IService<User> {
    Result add(User user);
    Result deletebyId(Integer id);
    Result updatebyId(User user);
    User findUserByusername(String username);
}
