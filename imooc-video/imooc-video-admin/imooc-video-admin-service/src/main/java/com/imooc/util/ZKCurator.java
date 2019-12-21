package com.imooc.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkImpl;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

public class ZKCurator {

    // zk客户端
    private CuratorFramework client = null;
    final static Logger logger = LoggerFactory.getLogger(ZKCurator.class);

    public ZKCurator (CuratorFramework client) {
        this.client = client;
    }

    public void init () {
        client = client.usingNamespace("admin");

        try {
            // 判断在admin命名空间下是否有bgm节点 /admin
            if (client.checkExists().forPath("/bgm") == null) {
                client.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        // 访问权限：匿名
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath("/bgm");
                logger.info("zookeeper初始化成功...");

                logger.info("zookeeper服务器状态：" + client.isStarted());
            }
        } catch (Exception e) {
            logger.error("zookeeper客户端连接、初始化错误...");
            e.printStackTrace();
        }
    }

    public void addBgmNode (String bgmId, String operatorObjStr) {
        try {
                client.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        // 访问权限：匿名
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath("/bgm/" + bgmId, operatorObjStr.getBytes("UTF-8"));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
