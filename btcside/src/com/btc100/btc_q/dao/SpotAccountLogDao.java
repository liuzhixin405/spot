package com.btc100.btc_q.dao;

public interface SpotAccountLogDao<T> extends BaseDaoI<T> {

	public com.btc100.btc_q.entity.SpotAccountLog insertLog(final com.btc100.btc_q.entity.SpotAccountLog accountLog);

	void createAccountLogBak(String code);

	void insertAccountLogBak(String code, long endTime);
	
	void deleteAccountLog(String code, long endTime);
}
