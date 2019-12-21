package com.imooc;


import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.imooc.config.ResourceConfig;
import com.imooc.enu.BGMOperatorTypeEnum;
import com.imooc.pojo.Bgm;
import com.imooc.service.BgmService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

@Component
public class ZKCuratorClient {

    // zk客户端
    private CuratorFramework client = null;
    final static Logger logger = LoggerFactory.getLogger(ZKCuratorClient.class);
//    final static String SERVICE_ZOOKEEPER = "192.168.93.128:2181";

    @Autowired
    private BgmService bgmService;

    @Autowired
    private ResourceConfig resourceConfig;

    private Gson gson = new Gson();

    public void init () {

        client = getCuratorFramework();
        client.start();
        logger.info("zookeeper状态：" + client.isStarted());
        client = client.usingNamespace("admin");
        try {
//            String data = new String(client.getData().forPath("/bgm/aaaa"));
//            logger.info("测试的值为：" + data);
            addChildrenWatch("/bgm");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void addChildrenWatch (String nodePath) throws Exception {

        PathChildrenCache cache = new PathChildrenCache(client,nodePath, true);
        cache.start();
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                if (pathChildrenCacheEvent.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {


                    // 准备进行下载管理系统的bgm
                    // 1. 拿到节点的路径的bgmid，获取bgm对象
                    String path = pathChildrenCacheEvent.getData().getPath();
                    String operatorObjStr = new String(pathChildrenCacheEvent.getData().getData());
                    Map<String,String> map = gson.fromJson(operatorObjStr, Map.class);
                    String operatorType = map.get("operType");
                    String bgmPath = map.get("path");

//                    String[] nodePath = path.split("/");
//                    String bgmId = nodePath[nodePath.length - 1];
//                    Bgm bgm = bgmService.queryByPrimaryKey(bgmId);
//                    if (bgm == null) {
//                        return;
//                    }
//                    String filePath = bgm.getPath();

                    // 2. 获取本地的下载路径
//                    String localPath = "D:\\imooc_video_dev" + bgmPath;
                    String localPath = resourceConfig.getLocalRealPath() + bgmPath;

                    // 3. 获取管理系统的播放地址, 并将斜杠转换为url的反斜杠，同时进行url编码
                    // \bgm\aaa.mp3
                    String finalPath = "";
                    String[] split = bgmPath.split("\\\\");
                    for (String str : split) {
                        if (StringUtils.isNotBlank(str)) {
                            finalPath += "/";
                            finalPath += URLEncoder.encode(str, "UTF-8");
                        }
                    }
//                    String fileUrl = "http://localhost:8080/mvc" + finalPath;
                    String fileUrl = resourceConfig.getAdminResourcePath() + finalPath;


                    if (operatorType.equals(BGMOperatorTypeEnum.ADD.type)) {
                        // 4. 封装url对象并使用apache的文件工具类进行文件拷贝
                        URL url = new URL(fileUrl);
                        File file = new File(localPath);
                        FileUtils.copyURLToFile(url,file);
                    } else {
                        File file = new File(localPath);
                        FileUtils.forceDelete(file);
                    }
                    client.delete().forPath(path);
                }
            }
        });
    }


    public RetryPolicy getRetryPolicy () {
        return new ExponentialBackoffRetry(1000,5);
    }

    public CuratorFramework getCuratorFramework () {
//        return CuratorFrameworkFactory.builder().connectString(SERVICE_ZOOKEEPER).sessionTimeoutMs(10000).
//                connectionTimeoutMs(10000).
//                retryPolicy(getRetryPolicy()).
//                namespace("admin").
//                build();
//        return CuratorFrameworkFactory.newClient(SERVICE_ZOOKEEPER,10000,10000,getRetryPolicy());
        return CuratorFrameworkFactory.newClient(resourceConfig.getZookeeperServer(),10000,10000,getRetryPolicy());
    }

    public static void main(String[] args) {
        String str = "\\bgm\\一路向北指弹.mp3";
        try {
            String finalPath = "";
            String[] split = str.split("\\\\");
            for (String str2 : split) {
                if (StringUtils.isNotBlank(str2)) {
                    finalPath += "/";
                    finalPath += URLEncoder.encode(str2, "UTF-8");
                }
            }
            System.out.println(finalPath);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
