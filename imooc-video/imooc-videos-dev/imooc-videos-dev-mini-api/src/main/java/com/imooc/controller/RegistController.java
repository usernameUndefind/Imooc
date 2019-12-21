package com.imooc.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UserService;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Api(value = "用户注册登录的接口", tags = {"注册和登录的controller"})
public class RegistController extends BasicController{

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户注册", notes = "用户注册的接口")
    @PostMapping("/regist")
    public IMoocJSONResult regist(@RequestBody Users user){

        // 1. 判断用户名和密码必须不为空
        if(StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())){
            return IMoocJSONResult.errorMsg("用户名和密码不能为空");
        }

        // 2. 判断用户名是否存在
        Users bean = userService.queryUserNameIsExist(user.getUsername(),null);
        if(bean == null){
            try {
                user.setNickname(user.getUsername());
                user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
                user.setFansCounts(0);
                user.setReceiveLikeCounts(0);
                user.setFollowCounts(0);
                userService.saveUser(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return IMoocJSONResult.errorMsg("用户名已经存在，请换一个再试");
        }
        user.setPassword("");

        return IMoocJSONResult.ok(setUserRedisSessionToken(user));

    }

    public UsersVO setUserRedisSessionToken(Users userModel){
        String uniqueToken = UUID.randomUUID().toString();
        redis.set(USER_REDIS_SESSION + ":" + userModel.getId(),uniqueToken,1000 * 60 *30);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userModel,usersVO);
        usersVO.setUserToken(uniqueToken);
        return usersVO;
    }

    @ApiOperation(value = "用户登录", notes = "用户登录接口")
    @PostMapping("/userLogin")
    public IMoocJSONResult login(@RequestBody Users user){
        Users bean = new Users();
        UsersVO usersVO = null;
        try {
            bean = userService.queryUserNameIsExist(user.getUsername(), MD5Utils.getMD5Str(user.getPassword()));
            usersVO = setUserRedisSessionToken(bean);
            if(bean == null){
                return IMoocJSONResult.errorMsg("用户名或密码错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        bean.setPassword("");
        return IMoocJSONResult.ok(usersVO);
    }

    @ApiOperation(value = "用户注销", notes = "用户注销的接口")
    @ApiImplicitParam(name = "userId", required = true, value = "用户id", dataType =
    "String", paramType = "query")
    @PostMapping("/logout")
    public IMoocJSONResult logout(String userId){
        redis.del(USER_REDIS_SESSION + ":" + userId);
        return IMoocJSONResult.ok();
    }

}
