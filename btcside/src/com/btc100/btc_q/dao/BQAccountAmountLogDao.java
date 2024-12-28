package com.btc100.btc_q.dao;

import java.util.List;

public interface BQAccountAmountLogDao<T> extends BaseDaoI<T> {

	List<Object[]> findSumZRZCRecord(long userId, int currencyId);
	
	List<Object[]> findSumZRZCOTCRecord(long userId, int currencyId);
	
	void createAccountAmountLogBak();

	void insertAccountAmountLogBak(long endTime);
	
	void deleteAccountAmountLog(long endTime);
}
