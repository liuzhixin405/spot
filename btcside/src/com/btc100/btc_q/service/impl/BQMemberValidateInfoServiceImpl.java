package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQMemberValidateInfoDao;
import com.btc100.btc_q.entity.BQMemberValidateInfo;
import com.btc100.btc_q.service.BQMemberValidateInfoService;

@Service("BQMemberValidateInfoService")
public class BQMemberValidateInfoServiceImpl implements BQMemberValidateInfoService {

	@Autowired
	private BQMemberValidateInfoDao<BQMemberValidateInfo> bqMemberValidateInfoDao;

	@Override
	public BQMemberValidateInfo readByUserId(long userId) {
		String hql = "from BQMemberValidateInfo t where t.user_id = :userId ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		return bqMemberValidateInfoDao.get(hql, params);
	}

	@Override
	public void save(BQMemberValidateInfo entity) {
		bqMemberValidateInfoDao.save(entity);
	}

	@Override
	public void update(BQMemberValidateInfo entity) {
		bqMemberValidateInfoDao.update(entity);
	}
	
}
