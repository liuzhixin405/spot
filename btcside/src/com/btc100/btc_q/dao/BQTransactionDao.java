package com.btc100.btc_q.dao;

public interface BQTransactionDao<T> extends BaseDaoI<T> {

	void createTransactionRelated(String code);

	T findTranByCode(String code);
}
