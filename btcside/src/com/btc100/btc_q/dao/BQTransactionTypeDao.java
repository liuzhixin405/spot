package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.BQTransactionType;

public interface BQTransactionTypeDao<T> extends BaseDaoI<T> {
	
	BQTransactionType getTransTypeByCode(String code);
	
	BQTransactionType getTransTypeById(int id);
	
}
