package com.imooc.service;

import com.imooc.pojo.Users;
import com.imooc.pojo.UsersReport;

public interface UserService {

    /**
     * 判断用户名是否存在
     * @param userName
     * @return
     */
     Users queryUserNameIsExist(String userName,String password);

    /**
     * 保存用户（用户注册）
     * @param user
     */
     void saveUser(Users user);


    /**
     *  用户修改信息
     * @param user
     */
     void updateUserInfo(Users user);


    /**
     * 查询用户信息
     * @param userId
     * @return
     */
    Users queryUsersInfo(String userId);

    /**
     * 查询用户是否点赞视频
     * @param userId
     * @param videoId
     * @return
     */
    boolean isUserLikeVideo(String userId, String videoId);


    /**
     * 增加用户和粉丝的关系
     * @param userId
     * @param fansId
     */
    void saveUserFanRelation(String userId, String fansId);

    /**
     * 删除用户和分析的关系
     * @param userId
     * @param fansId
     */
    void deleteUserFanRelation(String userId, String fansId);

    /**
     * 查询用户是否关注
     * @param userId
     * @param fanId
     * @return
     */
    boolean queryIsFollow(String userId, String fanId);

    /**
     * 保存用户举报信息
     * @param usersReport
     */
    void insertUserReport(UsersReport usersReport);
}
