package com.imooc.service;

import com.imooc.pojo.Bgm;
import com.imooc.pojo.Users;

import java.util.List;

public interface BgmService {

    /**
     * 查询背景音乐列表
     * @return
     */
    List<Bgm> queryBgmList();

    /**
     * 根据bgmid查询该bgm
     * @param bgmId
     * @return
     */
    Bgm queryByPrimaryKey (String  bgmId);
}
