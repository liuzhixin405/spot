package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.BQSpotMarket;

public interface BQSpotMarketDao<T> extends BaseDaoI<T> {
	
	BQSpotMarket getSpotMarketById(int id);

	BQSpotMarket getSpotMarketByName(String name);

	BQSpotMarket getSpotMarketByCoinName(String coinName);
}
