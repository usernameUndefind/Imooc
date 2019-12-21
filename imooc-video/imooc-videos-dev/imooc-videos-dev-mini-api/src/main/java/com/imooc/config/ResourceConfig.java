package com.imooc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties (prefix = "com.imooc")
@PropertySource("classpath:resourceConfig.properties")
public class ResourceConfig {

    private String zookeeperServer;
    private String localRealPath;
    private String adminResourcePath;


    public String getZookeeperServer() {
        return zookeeperServer;
    }

    public void setZookeeperServer(String zookeeperServer) {
        this.zookeeperServer = zookeeperServer;
    }

    public String getLocalRealPath() {
        return localRealPath;
    }

    public void setLocalRealPath(String localRealPath) {
        this.localRealPath = localRealPath;
    }

    public String getAdminResourcePath() {
        return adminResourcePath;
    }

    public void setAdminResourcePath(String adminResourcePath) {
        this.adminResourcePath = adminResourcePath;
    }

    @Override
    public String toString() {
        return "ResourceConfig{" +
                "zookeeperServer='" + zookeeperServer + '\'' +
                ", localRealPath='" + localRealPath + '\'' +
                ", adminResourcePath='" + adminResourcePath + '\'' +
                '}';
    }
}
