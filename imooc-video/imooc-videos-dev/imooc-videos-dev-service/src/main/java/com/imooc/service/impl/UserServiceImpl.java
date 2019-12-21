package com.imooc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.imooc.mapper.UsersFansMapper;
import com.imooc.mapper.UsersLikeVideosMapper;
import com.imooc.mapper.UsersMapper;
import com.imooc.mapper.UsersReportMapper;
import com.imooc.pojo.Users;
import com.imooc.pojo.UsersFans;
import com.imooc.pojo.UsersLikeVideos;
import com.imooc.pojo.UsersReport;
import com.imooc.service.UserService;
import com.imooc.utils.IMoocJSONResult;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import springfox.documentation.spring.web.json.Json;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UsersFansMapper usersFansMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersReportMapper usersReportMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserNameIsExist(String userName,String password) {
        Users user = new Users();
        user.setUsername(userName);
        user.setPassword(password);
        Users users = usersMapper.selectOne(user);
        return users;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUser(Users user) {
        user.setId(sid.nextShort());
        usersMapper.insert(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateUserInfo(Users user) {
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id", user.getId());
        usersMapper.updateByExampleSelective(user, userExample);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUsersInfo(String userId) {

        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id",userId);
        return usersMapper.selectOneByExample(userExample);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean isUserLikeVideo(String userId, String videoId) {

        boolean b = false;
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(videoId)) {
            return b;
        }
        Example example = new Example(UsersLikeVideos.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("videoId",videoId);

        List<UsersLikeVideos> usersLikeVideos = usersLikeVideosMapper.selectByExample(example);


        b = usersLikeVideos.size() > 0 ? true : false;
        return b;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUserFanRelation(String userId, String fansId) {
        // 1. 新增关系
        UsersFans usersFans = new UsersFans();
        usersFans.setId(sid.nextShort());
        usersFans.setUserId(userId);
        usersFans.setFanId(fansId);
        usersFansMapper.insert(usersFans);

        // 2. 粉丝数自增
        usersMapper.addFansCount(userId);
        // 3. 关注数自增
        usersMapper.addFollowerCount(fansId);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteUserFanRelation(String userId, String fansId) {
        // 1. 新增关系
        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("fanId",fansId);
        usersFansMapper.deleteByExample(example);

        // 2. 粉丝数累减
        usersMapper.reduceFansCount(userId);
        // 3. 关注数累减
        usersMapper.reduceFollowerCount(fansId);
    }


    @Override
    public boolean queryIsFollow(String userId, String fanId) {
        boolean b = false;
        // 1. 新增关系
        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("fanId",fanId);

        List<UsersFans> usersFans = usersFansMapper.selectByExample(example);
        b = usersFans.size() > 0 ? true : false;
        return b;
    }

    @Override
    public void insertUserReport(UsersReport usersReport) {

        Sid sid = new Sid();
        usersReport.setId(sid.nextShort());
        usersReport.setCreateDate(new Date());
        usersReportMapper.insert(usersReport);
    }
}
