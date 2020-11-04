package com.tan.trojangoplane.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tan.trojangoplane.mapper.UserMapper;
import com.tan.trojangoplane.model.pojo.Result;
import com.tan.trojangoplane.model.pojo.User;
import com.tan.trojangoplane.service.iwebUserService;
import com.tan.trojangoplane.trojango.callback.CallBack;
import com.tan.trojangoplane.trojango.trojanUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : tan
 * @date: 2020/11/4 12:02
 * @description :
 */
@Service
public class IwebUserServiceImpl extends ServiceImpl<UserMapper, User> implements iwebUserService {

    @Autowired
    private trojanUser trojanUser;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result add(User user) {
        //先往trojan插，再往mysql插入
        CallBack callBack = new CallBack();
        Result result = trojanUser.operationTrojanUser(callBack, user, 0);
        if(result.isFlag()){
            //trojango插入成功后往mysql中插
            user.setIsintrojan(1);
            try {
                User getuser = (User) trojanUser.findbypw(callBack, user.getPassword()).getData();
                //这里需要优化，自己计算哈希224，而不是从服务端获取
                user.setPwhash(getuser.getPwhash());
                userMapper.insert(user);
            } catch (Exception e) {
                e.printStackTrace();
                trojanUser.operationTrojanUser(callBack,user,1);
                return new Result(false,"操作mysql出错");
            }
            return new Result(true,"添加用户成功");
        }else {
            return result;
        }
    }

    @Override
    public Result deletebyId(Integer id) {
        //根据用户id删除用户
        //通过用户id从数据库中获取用户密码，利用密码在trojango中删除用户
        User user = userMapper.selectById(id);
        CallBack callBack = new CallBack();
        Result result = null;
        if(user != null){
            result = trojanUser.operationTrojanUser(callBack, user, 1);
        }else {
            return new Result(false,"不存在此用户");
        }

        if(result.isFlag()){
            //trojango中把用户删除成功
            //再在mysql中删除
            try {
                userMapper.deleteById(id);
            } catch (Exception e) {
                e.printStackTrace();
                //mysql操作出错，但trojango已经把用户删除了
                return new Result(false,"严重：trojango已经删除用户，mysql却没有删除，请自行在数据库中删除记录");
            }
            return new Result(true,"删除用户成功");
        }else {
            return result;
        }
    }

    @Override
    public Result updatebyId(User user) {
        //修改用户数据，不能修改密码
        //修改trojan限制条件+用户名
        //先修改trojango,再修改mysql
        CallBack callBack = new CallBack();
        Result result = null;
        if(user != null){
            result = trojanUser.operationTrojanUser(callBack, user, 2);
        }else {
            return new Result(false,"无修改参数");
        }

        if(result.isFlag()){
            //trojango服务端数据修改成功，进行mysql数据库修改
            try {
                //根据密码，密码唯一
                QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
                userQueryWrapper.eq("password",user.getPassword());
                userMapper.update(user,userQueryWrapper);
            } catch (Exception e) {
                e.printStackTrace();
                return new Result(false,"严重：trojango已经修改用户，mysql却没有修改，请自行在数据库中修改记录");
            }
            return new Result(true,"修改用户成功");
        }else {
            return result;
        }
    }

    @Override
    public User findUserByusername(String username) {
        //一般用于回显,也是先查trojango，再查mysql
        return null;
    }
}