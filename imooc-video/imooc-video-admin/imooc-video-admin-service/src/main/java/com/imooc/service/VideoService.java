package com.imooc.service;

import com.imooc.pojo.Bgm;
import com.imooc.utils.PagedResult;

public interface VideoService {

    void addBgm(Bgm bgm);

    PagedResult bgmList(Integer page, Integer pageSize);

    void delBgm(String id);
}
