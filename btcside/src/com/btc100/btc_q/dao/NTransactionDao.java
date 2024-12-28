package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.NTransaction;

public interface NTransactionDao<T> extends BaseDaoI<T> {

	void createTransactionRelated(String partition, String code);

	NTransaction findTranByCode(String partition, String code);
	
	public int insert(String partition, NTransaction trans);

	int updateSQL(String partition, NTransaction trans);
}
