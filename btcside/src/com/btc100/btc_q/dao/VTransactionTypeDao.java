package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.VTransactionType;

public interface VTransactionTypeDao<T> extends BaseDaoI<T> {
	
	VTransactionType getTransTypeByCode(String code);
	
	VTransactionType getTransTypeById(int id);
	
}
