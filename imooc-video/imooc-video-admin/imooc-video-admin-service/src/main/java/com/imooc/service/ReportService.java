package com.imooc.service;

import com.imooc.pojo.Bgm;
import com.imooc.utils.PagedResult;

public interface ReportService {

    PagedResult getReportList(Integer page, Integer pageSize);
    void updateVideoStatus (String videoId);
}
