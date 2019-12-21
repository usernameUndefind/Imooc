package com.imooc.controller;

import com.imooc.enu.VideoStatusEnum;
import com.imooc.pojo.Bgm;
import com.imooc.pojo.Comments;
import com.imooc.pojo.Users;
import com.imooc.pojo.Videos;
import com.imooc.service.BgmService;
import com.imooc.service.VideoService;
import com.imooc.utils.CoverPath;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MergeVideoMp3;
import com.imooc.utils.PagedResult;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

@RestController
@Api(value = "视频相关业务的接口", tags = {"视频相关业务的controller"})
@RequestMapping("/video")
public class VideoController extends BasicController{

    @Autowired
    private BgmService bgmService;

    @Autowired
    private VideoService videoService;

    @ApiOperation(value = "视频上传头像", notes = "视频上传的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", required = true, value = "用户id", dataType =
                    "String", paramType = "form"),
            @ApiImplicitParam(name = "bgmId", required = false, value = "背景音乐id", dataType =
                    "String", paramType = "form"),
            @ApiImplicitParam(name = "videoSeconds", required = true, value = "视频时长", dataType =
                    "String", paramType = "form"),
            @ApiImplicitParam(name = "videoWidth", required = true, value = "视频长度", dataType =
                    "String", paramType = "form"),
            @ApiImplicitParam(name = "videoHeight", required = true, value = "视频高度", dataType =
                    "String", paramType = "form"),
            @ApiImplicitParam(name = "desc", required = false, value = "视频描述", dataType =
                    "String", paramType = "form")
    })
    @PostMapping(value = "/upload", headers = "content-type=multipart/form-data")
    public IMoocJSONResult upload(String userId,
                                      String bgmId, double videoSeconds, int videoWidth, int videoHeight, String desc,
                                      @ApiParam(value = "短视频", required = true) MultipartFile videoFile) throws Exception {

        long startTime = System.currentTimeMillis();
        long time = startTime;
        if (StringUtils.isBlank(userId)) {
            return IMoocJSONResult.errorMsg("用户id不得为空");
        }

        String id = UUID.randomUUID().toString();
        // 保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/video";
        String coverPath = "/" + userId + "/video/" + id + ".jpg";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        String finalFacePath = "";
        String path = "";
        try {

            // TODO 先将文件保存到本地
            if (videoFile != null) {
                    // 文件上传的最终保存路径
                    path = "/" + id + ".mp4";
                    finalFacePath = FILE_SPACE + uploadPathDB + path;
                    // 设置数据库保存的路径
                    File outFile = new File(finalFacePath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = videoFile.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);

                    System.out.println("复制文件所花时间=" + (System.currentTimeMillis() - startTime) + "ms");
                    startTime = System.currentTimeMillis();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return IMoocJSONResult.errorMsg("上传出错");
        } finally {

            if (fileOutputStream != null) {
                fileOutputStream.close();
                inputStream.close();
            }
        }

        // TODO 判断bgmid是否为空，不为空则合并
        Videos videos = new Videos();
//        String videoOutputPath = FILE_SPACE + uploadPathDB;
        String videoId = "";

        // 判断bgmId 是否为空， 如果不为空则保存
        if (StringUtils.isNotBlank(bgmId)) {
            path = "/" + UUID.randomUUID().toString() + ".mp4";
            String videoOutDB = uploadPathDB + path;
            // 合并并保存数据库
            // 通过bgmid查询到该bgm的相对路径
            Bgm bgm = bgmService.queryByPrimaryKey(bgmId);
            // mp3的路径
            String mp3Path = FILE_SPACE + bgm.getPath();


            MergeVideoMp3 mergeVideoMp3 = new MergeVideoMp3(FFMEPG_PATH);

            mergeVideoMp3.convetor(finalFacePath,mp3Path,videoSeconds,FILE_SPACE + videoOutDB);
            System.out.println("合并视频和bgm所花时间=" + (System.currentTimeMillis() - startTime) + "ms");
            startTime = System.currentTimeMillis();
            videos.setAudioId(bgmId);
        }

        // TODO 生成实体类，插入数据库
        CoverPath cover = new CoverPath(FFMEPG_PATH);
        cover.convetor(finalFacePath,FILE_SPACE + coverPath);
        videos.setUserId(userId);
        videos.setVideoWidth(videoWidth);
        videos.setVideoHeight(videoHeight);
        videos.setVideoPath(uploadPathDB + path);
        videos.setVideoDesc(desc);
        videos.setCoverPath(coverPath);
        videos.setVideoSeconds((float) videoSeconds);
        videos.setStatus(VideoStatusEnum.SUCCESS.value);
        videos.setCreateTime(new Date());
        videoId = videoService.insertVideo(videos);

        System.out.println("上传视频所花总时间=" + (System.currentTimeMillis() - time) + "ms");
        return IMoocJSONResult.ok(videoId);
    }


    /**
     * TODO 分页和搜索查询视频列表
     * isSaveRecord:
     * 1 - 需要保存
     * 0 - 不需要保存或者为空的时候
     * @param videos
     * @param isSaveRecord
     * @param page
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/showAll")
    public IMoocJSONResult showAll(@RequestBody Videos videos, Integer isSaveRecord, Integer page) throws Exception {

        page = page == null ? 1 : page;
        PagedResult allVideos = videoService.getAllVideos(page, videos, isSaveRecord,PAGE_SIZE);
        return IMoocJSONResult.ok(allVideos);
    }

    /**
     * TODO 获取收藏的视频列表
     * @param userId
     * @param page
     * @return
     */
    @PostMapping(value = "/showLikeVideo")
    public IMoocJSONResult showLikeVideo(String userId, Integer page){
        page = page == null ? 1 : page;
        PagedResult allVideos = videoService.getLikeVideos(page,userId,PAGE_SIZE);
        return IMoocJSONResult.ok(allVideos);
    }

    /**
     * TODO 获取收藏的视频列表
     * @param userId
     * @param page
     * @return
     */
    @PostMapping(value = "/showFollowVideo")
    public IMoocJSONResult showFollowVideo(String userId, Integer page){
        page = page == null ? 1 : page;
        PagedResult allVideos = videoService.getFollowVideos(page,userId,PAGE_SIZE);
        return IMoocJSONResult.ok(allVideos);
    }



    /**
     * 获取热搜词列表
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/hot")
    public IMoocJSONResult hot() throws Exception {
        return IMoocJSONResult.ok(videoService.getHotWords());
    }


    /**
     * 获取热搜词列表
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/userLike")
    public IMoocJSONResult userLike(String userId, String videoId, String publisherId) throws Exception {
        videoService.userLikeVideo(userId, videoId, publisherId);
        return IMoocJSONResult.ok();
    }

    /**
     * 获取热搜词列表
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/userUnlike")
    public IMoocJSONResult userUnlike(String userId, String videoId, String publisherId) throws Exception {
        videoService.userUnlikeVideo(userId, videoId, publisherId);
        return IMoocJSONResult.ok();
    }

    /**
     * 保存视频评论
     * @param comments
     * @return
     */
    @PostMapping(value = "/saveComment")
    public IMoocJSONResult saveComment(@RequestBody Comments comments, String replyFatherCommentId, String replyToUserId) {
        if (StringUtils.isNotBlank(replyFatherCommentId) && StringUtils.isNotBlank(replyToUserId)) {
            comments.setFatherCommentId(replyFatherCommentId);
            comments.setToUserId(replyToUserId);
        }
        videoService.saveComment(comments);
        return IMoocJSONResult.ok();
    }


    @PostMapping(value = "/getVideoAllComments")
    public IMoocJSONResult getVideoAllComments(String videoId, Integer page, Integer pageSize){

        page = page == null ? 1 : page;
        pageSize = pageSize == null ? 10 : pageSize;
        PagedResult allVideoComments = videoService.getAllVideoComments(videoId, page, pageSize);

        return IMoocJSONResult.ok(allVideoComments);
    }
}
