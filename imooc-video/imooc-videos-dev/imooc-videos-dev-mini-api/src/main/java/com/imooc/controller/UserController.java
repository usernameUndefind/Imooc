package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.UsersReport;
import com.imooc.pojo.vo.PublisherVideo;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UserService;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.User;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@RestController
@Api(value = "用户业务操作的接口", tags = {"用户业务操作controller"})
@RequestMapping("/user")
public class UserController extends BasicController{


    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户上传头像", notes = "用户上传头像的接口")
    @ApiImplicitParam(name = "userId", required = true, value = "用户id", dataType =
            "String", paramType = "query")
    @PostMapping("/uploadFace")
    public IMoocJSONResult uploadFace(String userId,
                                      @RequestParam("file")MultipartFile[] files) throws Exception{


        if (StringUtils.isBlank(userId)) {
            return IMoocJSONResult.errorMsg("用户id不得为空");
        }
        // 文件保存的命名空间
        String fileSpace = "D:/imooc_video_dev";

        // 保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/face";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {

            if (files != null && files.length > 0) {


                String fileName = files[0].getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    // 文件上传的最终保存路径
                    String finalFacePath = fileSpace + uploadPathDB + "/" +fileName;

                    // 设置数据库保存的路径
                    uploadPathDB += ("/" +fileName);
                    File outFile = new File(finalFacePath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();

                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = files[0].getInputStream();
                    IOUtils.copy(inputStream,fileOutputStream);
                } else {
                    return IMoocJSONResult.errorMsg("上传出错");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return IMoocJSONResult.errorMsg("上传出错");
        } finally {

            if (fileOutputStream != null){
                fileOutputStream.close();
                inputStream.close();
            }
        }

        // 更新数据表的头像地址
        Users users = new Users();
        users.setId(userId);
        users.setFaceImage(uploadPathDB);
        userService.updateUserInfo(users);
        return IMoocJSONResult.ok(uploadPathDB);
    }

    @ApiOperation(value = "查询用户信息", notes = "查询用户的信息")
    @ApiImplicitParam(name = "userId", required = true, value = "用户id", dataType =
            "String", paramType = "query")
    @PostMapping("/query")
    public IMoocJSONResult query(String userId,String fanId) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return IMoocJSONResult.errorMsg("用户id不能为空");
        }

        Users users = userService.queryUsersInfo(userId);
        System.out.println("用户信息=" + users);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(users,usersVO);

        boolean b = userService.queryIsFollow(userId, fanId);
        usersVO.setFollow(b);
        return IMoocJSONResult.ok(usersVO);
    }



    @PostMapping("/queryPublisher")
    public IMoocJSONResult queryPublisher(String loginId,String videoId, String publisherId) throws Exception {

        if (StringUtils.isBlank(loginId) || StringUtils.isBlank(videoId) || StringUtils.isBlank(publisherId)) {
            return IMoocJSONResult.errorMsg("");
        }

        // 1. 查询视频发布者的信息
        Users users = userService.queryUsersInfo(publisherId);
        UsersVO publisher = new UsersVO();
        BeanUtils.copyProperties(users,publisher);

        // 2. 查询当前登陆者和视频的点赞关系
        boolean userLikeVideo = userService.isUserLikeVideo(loginId, videoId);

        PublisherVideo bean = new PublisherVideo();
        bean.setPublisher(publisher);
        bean.setUserLikeVideo(userLikeVideo);
        return IMoocJSONResult.ok(bean);
    }

    @PostMapping("/beyourfans")
    public IMoocJSONResult beyourfans(String userId, String fanId) throws Exception {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
            return IMoocJSONResult.errorMsg("");
        }
        userService.saveUserFanRelation(userId,fanId);
        return IMoocJSONResult.ok("关注成功...");
    }


    @PostMapping("/dontyourfans")
    public IMoocJSONResult dontyourfans(String userId, String fanId) throws Exception {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
            return IMoocJSONResult.errorMsg("");
        }

        userService.deleteUserFanRelation(userId,fanId);
        return IMoocJSONResult.ok("取消关注成功...");
    }

    @PostMapping("/reportUser")
    public IMoocJSONResult reportUser(@RequestBody UsersReport usersReport) {
        userService.insertUserReport(usersReport);
        return IMoocJSONResult.ok();
    }


}
