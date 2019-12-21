package com.imooc.service;

import com.imooc.pojo.Bgm;
import com.imooc.pojo.Comments;
import com.imooc.pojo.Videos;
import com.imooc.utils.PagedResult;

import java.util.List;

public interface VideoService {


    /**
     * 插入视频信息
     * @param videos
     * @return
     */
    String insertVideo (Videos videos);

    void insertCoverPath (String coverPath, String videoId);

    /**
     * 分页查询视频列表
     * @param page
     * @param pageSize
     * @return
     */
    PagedResult getAllVideos(Integer page, Videos videos, Integer isSaveRecord, Integer pageSize);

    /**
     * 获取收藏的视频列表
     * @param page
     * @param pageSize
     * @return
     */
    PagedResult getLikeVideos(Integer page, String userId, Integer pageSize);

    /**
     * 获取收藏的视频列表
     * @param page
     * @param pageSize
     * @return
     */
    PagedResult getFollowVideos(Integer page, String userId, Integer pageSize);

    /**
     * 获取热搜词s
     * @return
     */
    List<String> getHotWords ();

    /**
     * 累加获赞数
     * @param userId
     * @param videoId
     */
    void userLikeVideo(String userId,String videoId, String publisherId);

    /**
     * 累减获赞数
     * @param userId
     * @param videoId
     */
    void userUnlikeVideo(String userId,String videoId, String publisherId);

    /**
     * 保存评论
     * @param comments
     */
    void saveComment(Comments comments);

    /**
     * 获取视频的所有评论
     * @param videoId
     * @param page
     * @param pageSize
     * @return
     */
    PagedResult getAllVideoComments(String videoId, Integer page, Integer pageSize);
}
