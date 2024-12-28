package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.SpotAccountLog;

public interface SpotAccountLogService {

	public void insertLog(SpotAccountLog log);
	
	void createAccountLogBak(String code);

	void insertAccountLogBak(String code, long endTime);
}
