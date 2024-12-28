package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.TradeLogIncomeReconcilia;

public interface TradeLogIncomeReconciliaService {

	void add(TradeLogIncomeReconcilia obj);
	
	void update(TradeLogIncomeReconcilia obj);
	
	TradeLogIncomeReconcilia get();

	List<TradeLogIncomeReconcilia> findAll(int limit);

	Long getTime();
	
	TradeLogIncomeReconcilia getByUserId(long userId);
}
