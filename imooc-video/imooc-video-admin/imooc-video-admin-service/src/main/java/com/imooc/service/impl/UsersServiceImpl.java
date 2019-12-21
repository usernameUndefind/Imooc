package com.imooc.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.Users;
import com.imooc.pojo.UsersExample;
import com.imooc.pojo.UsersExample.Criteria;
import com.imooc.service.UsersService;
import com.imooc.utils.PagedResult;

@Service
public class UsersServiceImpl implements UsersService {

	@Autowired
	private UsersMapper userMapper;
	
	@Override
	public PagedResult queryUsers(Users user, Integer page, Integer pageSize) {

		PageHelper.startPage(page,pageSize);
		String username = "";
		String nickname = "";

		if (user != null) {
			username = user.getUsername();
			nickname = user.getNickname();
		}

		UsersExample example = new UsersExample();
		Criteria criteria = example.createCriteria();
		if (StringUtils.isNotBlank(username)) {
			criteria.andUsernameLike("%" + username + "%");
		}
		if (StringUtils.isNotBlank(nickname)) {
			criteria.andNicknameLike("%" + nickname + "%");
		}
		List<Users> users = userMapper.selectByExample(example);

		PageInfo<Users> pageInfo = new PageInfo<>(users);
		PagedResult pagedResult = new PagedResult();
		pagedResult.setRows(users);
		pagedResult.setTotal(pageInfo.getPages());        // 总页数
		pagedResult.setRecords(pageInfo.getTotal());      // 总记录数
		pagedResult.setPage(page);                        // 当前页
		return pagedResult;
	}


}
