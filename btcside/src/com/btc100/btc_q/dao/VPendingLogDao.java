package com.btc100.btc_q.dao;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.VPendingLog;

public interface VPendingLogDao<T> extends BaseDaoI<T> {

	void insertLog(VPendingLog log);

	void updateActionByPendingNo(String bqCode, long pendingNo, long userId, int action);
	
	void updateUndealByPendingNo(String bqCode, long pendingNo, long userId, BigDecimal price, BigDecimal dealCount,
			BigDecimal income, BigDecimal fee, BigDecimal bfxFee);
	
	int jsCancelOrder(String vCode, int action, int pripority, int direction);

	int jsCancelOrderForCache(String vCode, int action, int pripority, int direction);

	void createPendingLogBak(String code);
	
	void insertPendingLogBak(String code, long endTime);
	
	void deletePendingLog(String code, long endTime);
}
