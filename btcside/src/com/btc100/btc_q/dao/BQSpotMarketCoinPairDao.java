package com.btc100.btc_q.dao;

import java.util.List;

import com.btc100.btc_q.entity.BQSpotMarketCoinPair;

public interface BQSpotMarketCoinPairDao<T> extends BaseDaoI<T> {

	void createTransactionRelated(String coinPairName);
	
	BQSpotMarketCoinPair findByCoinPairName(String coinPairName);
	
	List<BQSpotMarketCoinPair> findByCoinId(int currencyId);
	
	List<BQSpotMarketCoinPair> findByMarketCoinId(int currencyId);
}
