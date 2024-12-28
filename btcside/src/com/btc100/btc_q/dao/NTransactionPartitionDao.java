package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.NTransactionPartition;

public interface NTransactionPartitionDao<T> extends BaseDaoI<T> {
	
	int insert(String partition, NTransactionPartition part);

	int update(String partition, NTransactionPartition part);
	
}
