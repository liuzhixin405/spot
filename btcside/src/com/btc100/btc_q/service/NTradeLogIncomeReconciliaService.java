package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.NTradeLogIncomeReconcilia;

public interface NTradeLogIncomeReconciliaService {

	void add(String partition, NTradeLogIncomeReconcilia obj);
	
	void update(String partition, NTradeLogIncomeReconcilia obj);
	
	NTradeLogIncomeReconcilia get(String partition);

	List<NTradeLogIncomeReconcilia> findAll(String partition, int limit);

	Long getTime();
	
	NTradeLogIncomeReconcilia getByUserId(String partition, long userId);
}
