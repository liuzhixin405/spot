package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.DeliveryTradeLogReconcilia;

public interface DeliveryTradeLogReconciliaDao<T> extends BaseDaoI<T> {

	void insert(DeliveryTradeLogReconcilia e);
	
	DeliveryTradeLogReconcilia getByTimeAndUser(String code, long time, long userId);
	
	DeliveryTradeLogReconcilia getByUser(String code, long userId);

	Object[] getIncomeAndFeeByTimeAndUser(String bqCode, long userId, long startTime, long endTime);
}
