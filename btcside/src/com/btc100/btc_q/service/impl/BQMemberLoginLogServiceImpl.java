package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQMemberLoginLogDao;
import com.btc100.btc_q.entity.BQMemberLoginLog;
import com.btc100.btc_q.service.BQMemberLoginLogService;

@Service("bqMemberLoginLogService")
public class BQMemberLoginLogServiceImpl implements BQMemberLoginLogService {

	@Autowired
	private BQMemberLoginLogDao<BQMemberLoginLog> bqMemberLoginLogDao;

	public void save(BQMemberLoginLog bqMemberLoginLog) {
		bqMemberLoginLogDao.save(bqMemberLoginLog);
	}

	@Override
	public Long getTotalLoginUserByTime(Long startTime, Long endTime) {
		return bqMemberLoginLogDao.getTotalLoginUserByTime(startTime, endTime);
	}

	@Override
	public List<BQMemberLoginLog> readTodayByUserId(long startTime, long endTime, long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("startTime", startTime);
		params.put("endTime", endTime);
		params.put("userId", userId);
		String hql = "FROM BQMemberLoginLog WHERE login_time > :startTime AND login_time <= :endTime AND f_user_id = :userId";
		return bqMemberLoginLogDao.find(hql, params);
	}

}
