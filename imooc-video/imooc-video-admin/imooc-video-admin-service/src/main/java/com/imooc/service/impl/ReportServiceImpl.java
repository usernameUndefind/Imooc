package com.imooc.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.enums.VideoOperEnum;
import com.imooc.mapper.ReportMapperCustom;
import com.imooc.mapper.VideosMapper;
import com.imooc.pojo.Videos;
import com.imooc.pojo.vo.ReportUser;
import com.imooc.service.ReportService;
import com.imooc.utils.PagedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapperCustom reportMapperCustom;

    @Autowired
    private VideosMapper videosMapper;


    @Override
    public PagedResult getReportList(Integer page, Integer pageSize) {

        PageHelper.startPage(page,pageSize);

        List<ReportUser> allReportUser = reportMapperCustom.getAllReportUser();

        PageInfo<ReportUser> pageInfo = new PageInfo<>(allReportUser);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setRows(allReportUser);
        pagedResult.setRecords(pageInfo.getTotal());   // 总记录数
        pagedResult.setTotal(pageInfo.getPages());     // 总页数
        pagedResult.setPage(page);
        return pagedResult;
    }

    @Override
    public void updateVideoStatus(String videoId) {
        Videos videos = new Videos();
        videos.setId(videoId);
        videos.setStatus(VideoOperEnum.FORBIDDEN.type);
        videosMapper.updateByPrimaryKeySelective(videos);
    }
}
