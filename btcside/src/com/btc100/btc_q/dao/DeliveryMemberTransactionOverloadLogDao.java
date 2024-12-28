package com.btc100.btc_q.dao;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.DeliveryMemberTransactionOverloadLog;

public interface DeliveryMemberTransactionOverloadLogDao<T> extends BaseDaoI<T> { 
	
	DeliveryMemberTransactionOverloadLog getByPendingNo(String code, long uid, long pendingNo);
	
	void addBalance(String code, long uid, long pendingNo, BigDecimal change);
}
