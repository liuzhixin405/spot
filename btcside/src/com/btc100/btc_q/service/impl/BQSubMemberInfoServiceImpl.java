package com.btc100.btc_q.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQSubMemberInfoDao;
import com.btc100.btc_q.entity.BQSubMemberInfo;
import com.btc100.btc_q.service.BQSubMemberInfoService;

@Service("bqSubMemberInfoService")
public class BQSubMemberInfoServiceImpl implements BQSubMemberInfoService {

	@Autowired
	private BQSubMemberInfoDao<BQSubMemberInfo> bqSubMemberInfoDao;

	public void save(BQSubMemberInfo info) {
		bqSubMemberInfoDao.save(info);
	}

	@Override
	public void update(BQSubMemberInfo info) {
		bqSubMemberInfoDao.update(info);
	}

}
