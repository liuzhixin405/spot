package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.NTradeLogIncomeReconcilia;

public interface NTradeLogIncomeReconciliaDao<T> extends BaseDaoI<T> {
	
	public int insert(String partition, NTradeLogIncomeReconcilia log);

	public int update(String partition, NTradeLogIncomeReconcilia log);

}
