package com.imooc.controller;

import com.imooc.service.ReportService;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.PagedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("report")
public class ReportController {


    @Autowired
    private ReportService reportService;

    @RequestMapping("showList")
    public String showList () {
        return "video/reportList";
    }

    @RequestMapping("list")
    @ResponseBody
    public PagedResult list (Integer page) {
        PagedResult pagedResult = null;
        try {
            pagedResult = reportService.getReportList(page == null ? 1 : page, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pagedResult;
    }

    @RequestMapping("updateVideoStatus")
    @ResponseBody
    public IMoocJSONResult updateVideoStatus (String videoId) {
        reportService.updateVideoStatus(videoId);
        return IMoocJSONResult.ok();
    }
}
