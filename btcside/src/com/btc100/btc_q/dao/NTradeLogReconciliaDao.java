package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.NTradeLogReconcilia;

public interface NTradeLogReconciliaDao<T> extends BaseDaoI<T> {

	void insert(String partition, NTradeLogReconcilia e);
	
	void update(String partition, NTradeLogReconcilia e);
	
	NTradeLogReconcilia getByTimeAndUser(String partition, String code, long time, long userId);
	
	NTradeLogReconcilia getByUser(String partition, String code, long userId);

	Object[] getIncomeAndFeeByTimeAndUser(String partition, String bqCode, long userId, long startTime, long endTime);
}
