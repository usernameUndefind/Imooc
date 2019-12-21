package com.imooc.mapper;

import com.imooc.pojo.Videos;
import com.imooc.pojo.vo.VideosVO;
import com.imooc.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VideosMapperCustom extends MyMapper<Videos> {

    public List<VideosVO> queryVideoList (@Param("videoDesc") String videoDesc, @Param("userId") String userId);

    public List<VideosVO> queryLikeVideoList(@Param("userId") String userId);

    public List<VideosVO> queryFollowVideoList(@Param("userId") String userId);

    public void addVideoLikeCount(String videoId);

    public void reduceVideoLikeCount(String videoId);
}