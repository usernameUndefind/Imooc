package com.imooc.controller;

import com.imooc.pojo.Bgm;
import com.imooc.pojo.Users;
import com.imooc.service.VideoService;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.PagedResult;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @RequestMapping("showAddBgm")
    public String showAddBgm () {
        return "video/addBgm";
    }

    @RequestMapping("showBgmList")
    public String showBgmList () {
        return "video/bgmList";
    }

    @RequestMapping("bgmList")
    @ResponseBody
    public PagedResult addBgm (Integer page) {
        return videoService.bgmList(page, 10);
    }

    @RequestMapping("delBgm")
    @ResponseBody
    public IMoocJSONResult delBgm (String id) {
        videoService.delBgm(id);
        return IMoocJSONResult.ok();
    }

    @RequestMapping("addBgm")
    @ResponseBody
    public IMoocJSONResult addBgm (Bgm bgm) {

        try{
            videoService.addBgm(bgm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return IMoocJSONResult.ok();
    }


    @PostMapping("/bgmUpload")
    @ResponseBody
    public IMoocJSONResult uploadFace(@RequestParam("file") MultipartFile[] files) throws Exception{


        // 文件保存的命名空间
//        String fileSpace = "D:/imooc_video_dev";
//        String fileSpace = File.separator + "imooc_video_dev" + File.separator + "mvc-bgm";
        String fileSpace = "D:" + File.separator + "imooc_video_dev" + File.separator + "mvc-bgm";
        // 保存到数据库中的相对路径
        String uploadPathDB = File.separator +  "bgm";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {

            if (files != null && files.length > 0) {


                String fileName = files[0].getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    // 文件上传的最终保存路径
                    String finalPath = fileSpace + uploadPathDB + File.separator +fileName;

                    // 设置数据库保存的路径
                    uploadPathDB += (File.separator +fileName);
                    File outFile = new File(finalPath);
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

        return IMoocJSONResult.ok(uploadPathDB);
    }
}
