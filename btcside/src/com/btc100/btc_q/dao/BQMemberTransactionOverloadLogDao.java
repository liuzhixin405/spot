package com.btc100.btc_q.dao;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.BQMemberTransactionOverloadLog;

public interface BQMemberTransactionOverloadLogDao<T> extends BaseDaoI<T> { 
	
	BQMemberTransactionOverloadLog getByPendingNo(String code, long uid, long pendingNo);
	
	void addBalance(String code, long uid, long pendingNo, BigDecimal change);
}
