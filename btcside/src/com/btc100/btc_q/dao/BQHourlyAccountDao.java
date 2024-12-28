package com.btc100.btc_q.dao;

public interface BQHourlyAccountDao<T> extends BaseDaoI<T> {

	void createHourlyAccountBak();

	void insertHourlyAccountBak(long endTime);
	
	void deleteHourlyAccount(long endTime);
}
