package com.imooc.mapper;

import com.imooc.pojo.Users;
import com.imooc.utils.MyMapper;

public interface UsersMapper extends MyMapper<Users> {

    /**
     * 用户受喜欢数累加
     * @param userId
     */
    public void addReceiveLikeCount(String userId);

    /**
     * 用户受喜欢数累减
     * @param userId
     */
    public void reduceReceiveLikeCount(String userId);

    /**
     * 增加粉丝数量
     * @param userId
     */
    public void addFansCount(String userId);

    /**
     * 新增关注数量
     * @param userId
     */
    public void addFollowerCount(String userId);

    /**
     * 减少粉丝数
     * @param userId
     */
    public void reduceFansCount(String userId);

    /**
     * 减少关注数量
     * @param userId
     */
    public void reduceFollowerCount(String userId);

}