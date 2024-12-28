package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQMemberAreaDao;

import com.btc100.btc_q.entity.BQMemberArea;
import com.btc100.btc_q.service.BQMemberAreaService;

@Service("bqMemberAreaService")
public class BQMemberAreaServiceImpl implements BQMemberAreaService {
	
	@Autowired
	private BQMemberAreaDao<BQMemberArea> bqMemberAreaDao;
	public void save(BQMemberArea bqA) {
		bqMemberAreaDao.save(bqA);
	}
	
	public BQMemberArea findByUserId(long fUserId) {
		final String hql = "from BQMemberArea u where u.f_user_id = :id";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", fUserId);
		BQMemberArea user = (BQMemberArea) bqMemberAreaDao.get(hql, paramMap);
		return user;
	}
	
	public BQMemberArea readByUserId(long fUserId) {
		final String hql = "from BQMemberArea u where u.f_user_id = :id";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", fUserId);
		BQMemberArea user = (BQMemberArea) bqMemberAreaDao.get(hql, paramMap);
		return user;
	}

}
