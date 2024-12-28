package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.NStopLoss;

public interface NStopLossDao<T> extends BaseDaoI<T> {

	void deleteByTransactionMemberId(String partition, Long transactionMemberId); 

	public int insert(String partition, NStopLoss stopLoss);

}
