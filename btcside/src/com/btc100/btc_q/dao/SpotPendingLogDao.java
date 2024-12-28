package com.btc100.btc_q.dao;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.SpotPendingLog;

public interface SpotPendingLogDao<T> extends BaseDaoI<T> {

	void insertLog(SpotPendingLog log);

	void createPendingLogBak(String code);
	
	void insertPendingLogBak(String code, long endTime);
	
	void deletePendingLog(String code, long endTime);
	
	void updateActionByPendingNo(String bqCode, long pendingNo, long userId, int action);
	
	void updateUndealByPendingNo(String bqCode, long pendingNo, long userId, BigDecimal price, BigDecimal dealCount,
			BigDecimal income, BigDecimal fee, BigDecimal bfxFee);
}
