package com.imooc.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mapper.*;
import com.imooc.pojo.*;
import com.imooc.pojo.vo.CommentsVO;
import com.imooc.pojo.vo.VideosVO;
import com.imooc.service.BgmService;
import com.imooc.service.VideoService;
import com.imooc.utils.PagedResult;
import com.imooc.utils.TimeAgoUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private VideosMapperCustom videosMapperCustom;

    @Autowired
    private SearchRecordsMapper searchRecordsMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private CommentsMapper commentsMapper;

    @Autowired
    private CommentsMapperCustom commentsMapperCustom;

    @Autowired
    private Sid sid;

    @Transactional (propagation = Propagation.REQUIRED)
    @Override
    public String insertVideo(Videos videos) {
        String id = sid.nextShort();
        videos.setId(id);
        videosMapper.insertSelective(videos);
        return id;
    }

    @Transactional (propagation = Propagation.REQUIRED)
    @Override
    public void insertCoverPath(String coverPath, String videoId) {
        Videos videos = new Videos();
        videos.setId(videoId);
        videos.setCoverPath(coverPath);
        int i = videosMapper.updateByPrimaryKeySelective(videos);
        System.out.println("更新数据库=" + i);
    }

    @Transactional (propagation = Propagation.REQUIRED)
    @Override
    public PagedResult getAllVideos(Integer page, Videos videos, Integer isSaveRecord, Integer pageSize) {


        String desc = videos.getVideoDesc();
        // 需要保存热搜词
        if (isSaveRecord != null && isSaveRecord == 1) {
            SearchRecords records = new SearchRecords();
            String recordId = sid.nextShort();
            records.setId(recordId);
            records.setContent(desc);
            searchRecordsMapper.insert(records);
        }

        PageHelper.startPage(page,pageSize);
        String userId = videos.getUserId();
        List<VideosVO> list = videosMapperCustom.queryVideoList(desc,userId);
        PageInfo<VideosVO> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageList.getTotal());
        return pagedResult;
    }

    @Override
    public PagedResult getLikeVideos(Integer page, String userId, Integer pageSize) {

        PageHelper.startPage(page,pageSize);
        List<VideosVO> list = videosMapperCustom.queryLikeVideoList(userId);
        PageInfo<VideosVO> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageList.getTotal());
        return pagedResult;
    }

    @Override
    public PagedResult getFollowVideos(Integer page, String userId, Integer pageSize) {


        PageHelper.startPage(page,pageSize);
        List<VideosVO> list = videosMapperCustom.queryFollowVideoList(userId);
        PageInfo<VideosVO> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageList.getTotal());
        return pagedResult;
    }

    @Transactional (propagation = Propagation.SUPPORTS)
    @Override
    public List<String> getHotWords() {
        return searchRecordsMapper.getHotWords();
    }

    @Transactional (propagation = Propagation.REQUIRED)
    @Override
    public void userLikeVideo(String userId, String videoId, String publisherId) {
        // 1. 保存用户和视频的喜欢点赞关联关系表
        String likeId = sid.nextShort();
        UsersLikeVideos usersLikeVideos = new UsersLikeVideos();
        usersLikeVideos.setId(likeId);
        usersLikeVideos.setUserId(userId);
        usersLikeVideos.setVideoId(videoId);
        usersLikeVideosMapper.insert(usersLikeVideos);

        // 2. 视频喜欢数量累加
        videosMapperCustom.addVideoLikeCount(videoId);

        // 3. 用户受喜欢数量的累加
        usersMapper.addReceiveLikeCount(userId);

    }

    @Transactional (propagation = Propagation.REQUIRED)
    @Override
    public void userUnlikeVideo(String userId, String videoId, String publisherId) {
        // 1. 删除用户和视频的喜欢点赞关联关系表
        Example example = new Example(UsersLikeVideos.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("videoId",videoId);
        usersLikeVideosMapper.deleteByExample(example);

        // 2. 视频喜欢数量累减
        videosMapperCustom.reduceVideoLikeCount(videoId);

        // 3. 用户受喜欢数量的累减
        usersMapper.reduceReceiveLikeCount(userId);
    }

    @Override
    public void saveComment(Comments comments) {
        comments.setId(new Sid().nextShort());
        comments.setCreateTime(new Date());
        commentsMapper.insert(comments);
    }

    @Override
    public PagedResult getAllVideoComments(String videoId, Integer page, Integer pageSize) {

        PageHelper.startPage(page,pageSize);
        List<CommentsVO> videoAllComments = commentsMapperCustom.getVideoAllComments(videoId);

        // TODO 需要将评论里面的时间改成用户适应
        for (CommentsVO comments: videoAllComments ) {
            comments.setTimeAgo(TimeAgoUtils.format(comments.getCreateTime()));
        }

        PageInfo<CommentsVO> pageList = new PageInfo<>(videoAllComments);
        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(videoAllComments);
        pagedResult.setRecords(pageList.getTotal());
        return pagedResult;
    }
}
