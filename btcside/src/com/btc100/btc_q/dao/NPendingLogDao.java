package com.btc100.btc_q.dao;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.NPendingLog;

public interface NPendingLogDao<T> extends BaseDaoI<T> {

	void insertLog(String partition, NPendingLog log);

	void updateActionByPendingNo(String partition, String bqCode, long pendingNo, long userId, int action);
	
	void updateUndealByPendingNo(String partition, String bqCode, long pendingNo, long userId, BigDecimal price, BigDecimal dealCount,
			BigDecimal income, BigDecimal fee, BigDecimal bfxFee);
	
	int jsCancelOrder(String partition, String vCode, int action, int pripority, int direction);

	int jsCancelOrderForCache(String partition, String vCode, int action, int pripority, int direction);

	void createPendingLogBak(String partition, String code);
	
	void insertPendingLogBak(String partition, String code, long endTime);
	
	void deletePendingLog(String partition, String code, long endTime);
}
