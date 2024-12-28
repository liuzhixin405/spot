package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.DeliveryTransactionType;

public interface DeliveryTransactionTypeDao<T> extends BaseDaoI<T> {
	
	DeliveryTransactionType getTransTypeByCode(String code);
	
	DeliveryTransactionType getTransTypeById(int id);
	
}
