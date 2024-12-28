package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.NTransactionType;

public interface NTransactionTypeDao<T> extends BaseDaoI<T> {
	
	NTransactionType getTransTypeByCode(String partition, String code);
	
	NTransactionType getTransTypeById(String partition, int id);
	
	public int insert(String partition, NTransactionType transType);
	
	public int update(String partition, NTransactionType transType);
	
}
