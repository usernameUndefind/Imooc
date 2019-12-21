package com.imooc.pojo.vo;

import java.util.Date;

public class ReportUser {

    private String id;
    private String reportType;
    private String reportContent;
    private String dealUserName;
    private String dealVideoId;
    private String dealVideoPath;
    private Integer videoStatus;
    private String submitUserName;
    private Date createTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getReportContent() {
        return reportContent;
    }

    public void setReportContent(String reportContent) {
        this.reportContent = reportContent;
    }


    public String getDealVideoId() {
        return dealVideoId;
    }

    public void setDealVideoId(String dealVideoId) {
        this.dealVideoId = dealVideoId;
    }

    public String getDealVideoPath() {
        return dealVideoPath;
    }

    public void setDealVideoPath(String dealVideoPath) {
        this.dealVideoPath = dealVideoPath;
    }

    public Integer getVideoStatus() {
        return videoStatus;
    }

    public void setVideoStatus(Integer videoStatus) {
        this.videoStatus = videoStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDealUserName() {
        return dealUserName;
    }

    public void setDealUserName(String dealUserName) {
        this.dealUserName = dealUserName;
    }

    public String getSubmitUserName() {
        return submitUserName;
    }

    public void setSubmitUserName(String submitUserName) {
        this.submitUserName = submitUserName;
    }

    @Override
    public String toString() {
        return "ReportUser{" +
                "id='" + id + '\'' +
                ", reportType='" + reportType + '\'' +
                ", reportContent='" + reportContent + '\'' +
                ", dealUserName='" + dealUserName + '\'' +
                ", dealVideoId='" + dealVideoId + '\'' +
                ", dealVideoPath='" + dealVideoPath + '\'' +
                ", videoStatus='" + videoStatus + '\'' +
                ", submitUserName='" + submitUserName + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
