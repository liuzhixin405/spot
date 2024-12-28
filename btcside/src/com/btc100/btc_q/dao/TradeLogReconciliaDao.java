package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.TradeLogReconcilia;

public interface TradeLogReconciliaDao<T> extends BaseDaoI<T> {

	void insert(TradeLogReconcilia e);
	
	TradeLogReconcilia getByTimeAndUser(String code, long time, long userId);
	
	TradeLogReconcilia getByUser(String code, long userId);

	Object[] getIncomeAndFeeByTimeAndUser(String bqCode, long userId, long startTime, long endTime);
}
