package com.btc100.btc_q.dao;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.DeliveryPendingLog;

public interface DeliveryPendingLogDao<T> extends BaseDaoI<T> {

	void insertLog(DeliveryPendingLog log);
	
	void updateActionByPendingNo(String bqCode, long pendingNo, long userId, int action);
	
	void updateUndealByPendingNo(String bqCode, long pendingNo, long userId, BigDecimal price, BigDecimal dealCount, BigDecimal income, BigDecimal fee, BigDecimal bfxFee);

	int jsCancelOrder(String bqCode, int action, int pripority, int direction);

	int jsCancelOrderForCache(String bqCode, int action, int pripority, int direction);
	
	void createDeliveryPendingLogBak(String code);
	
	void insertDeliveryPendingLogBak(String code, long endTime);
	
	void deleteDeliveryPendingLog(String code, long endTime);
}
