package com.btc100.btc_q.dao;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.VMemberTransactionOverloadLog;

public interface VMemberTransactionOverloadLogDao<T> extends BaseDaoI<T> {
	
	VMemberTransactionOverloadLog getByPendingNo(String code, long uid, long pendingNo);
	
	void addBalance(String code, long uid, long pendingNo, BigDecimal change);
}
