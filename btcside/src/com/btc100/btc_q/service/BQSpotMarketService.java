package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQSpotMarket;
import com.btc100.btc_q.vo.PagerModel;

public interface BQSpotMarketService {
	
	BQSpotMarket getSpotMarketByName(String name);
	
	BQSpotMarket getSpotMarketByCoinName(String coinName);
	
	List<BQSpotMarket> findAllUsing();
	
	void updateSpotMarket(BQSpotMarket market);
	
	
	/************************************ for read only **************************************/
	
	BQSpotMarket readSpotMarketById(int id);
	
	List<BQSpotMarket> readAllUsing();
	
	PagerModel readPagerModel();
	
	BQSpotMarket readMarket(int marketId);
}
