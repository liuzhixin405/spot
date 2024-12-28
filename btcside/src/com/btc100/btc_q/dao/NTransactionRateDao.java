package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.NTransactionRate;

public interface NTransactionRateDao<T> extends BaseDaoI<T> {
	
	public int insert(String partition, NTransactionRate form);
	
	public int update(String partition, NTransactionRate form);
	
}
