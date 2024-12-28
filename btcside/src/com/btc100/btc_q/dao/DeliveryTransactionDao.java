package com.btc100.btc_q.dao;

public interface DeliveryTransactionDao<T> extends BaseDaoI<T> {

	void createTransactionRelated(String transactioCode, String code, String deliveryType);

	T findTranByCode(String code);
	
	int closeTransaction(String transactioCode);
}
