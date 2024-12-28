package com.btc100.btc_q.dao;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.NMemberTransactionOverloadLog;

public interface NMemberTransactionOverloadLogDao<T> extends BaseDaoI<T> {
	
	NMemberTransactionOverloadLog getByPendingNo(String partition, String code, long uid, long pendingNo);
	
	void addBalance(String partition, String code, long uid, long pendingNo, BigDecimal change);
	
	int insert(String partition, NMemberTransactionOverloadLog log);
}
