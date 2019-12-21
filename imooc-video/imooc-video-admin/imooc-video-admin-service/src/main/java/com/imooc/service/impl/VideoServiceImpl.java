package com.imooc.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.imooc.enums.BGMOperatorTypeEnum;
import com.imooc.mapper.BgmMapper;
import com.imooc.pojo.Bgm;
import com.imooc.pojo.BgmExample;
import com.imooc.service.VideoService;
import com.imooc.util.ZKCurator;
import com.imooc.utils.PagedResult;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private BgmMapper bgmMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private ZKCurator zkCurator;

    private Gson gson = new Gson();

    @Override
    public void addBgm(Bgm bgm) {
        bgm.setId(sid.nextShort());
        bgmMapper.insertSelective(bgm);
        Map<String,String> map = new HashMap<>();
        map.put("operType",BGMOperatorTypeEnum.ADD.type);
        map.put("path",bgm.getPath());
        String param = gson.toJson(map);
        zkCurator.addBgmNode(bgm.getId(), param);
    }

    @Override
    public PagedResult bgmList(Integer page, Integer pageSize) {

        PageHelper.startPage(page, pageSize);

        List<Bgm> bgms = bgmMapper.selectByExample(new BgmExample());

        PageInfo<Bgm> bgmList = new PageInfo<>(bgms);
        PagedResult result = new PagedResult();
        result.setRows(bgms);
        result.setRecords(bgmList.getTotal());
        result.setTotal(bgmList.getPages());
        result.setPage(page);
        return result;
    }

    @Override
    public void delBgm(String id) {
        Bgm bgm = bgmMapper.selectByPrimaryKey(id);
        bgmMapper.deleteByPrimaryKey(id);
        Map<String,String> map = new HashMap<>();
        map.put("operType",BGMOperatorTypeEnum.DELETE.type);
        map.put("path",bgm.getPath());
        zkCurator.addBgmNode(id, gson.toJson(map));
    }
}
