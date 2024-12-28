package com.btc100.btc_q.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.SpotAccountLogDao;
import com.btc100.btc_q.entity.SpotAccountLog;
import com.btc100.btc_q.service.SpotAccountLogService;

@Service("spotAccountLogService")
public class SpotAccountLogServiceImpl implements SpotAccountLogService {

	@Autowired
	private SpotAccountLogDao<SpotAccountLog> spotAccountLogDao;

	@Override
	public void insertLog(SpotAccountLog log) {
		log.setDateline(spotAccountLogDao.getUnix_TIMESTAMP());
		spotAccountLogDao.insertLog(log);
	}

	@Override
	public void createAccountLogBak(String code) {
		spotAccountLogDao.createAccountLogBak(code);
	}

	@Override
	public void insertAccountLogBak(String code, long endTime) {
		spotAccountLogDao.insertAccountLogBak(code, endTime);
		spotAccountLogDao.deleteAccountLog(code, endTime);
	}

}
