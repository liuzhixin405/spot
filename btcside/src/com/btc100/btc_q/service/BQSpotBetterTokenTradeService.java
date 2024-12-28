package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.BQSpotBetterTokenTrade;

public interface BQSpotBetterTokenTradeService {

	BQSpotBetterTokenTrade getByOrderId(Long orderId);

	void save(BQSpotBetterTokenTrade order);
	
	void update(BQSpotBetterTokenTrade order);
	
	List<BQSpotBetterTokenTrade> findTradesAboveId(Long orderId);
	
	List<BQSpotBetterTokenTrade> findTrades(long startTime, long endTime);
	
	BigDecimal sumAmountNotSelf(long startTime, long endTime, String side, BigDecimal price);
	
	/************************************ for read only **************************************/
	
}

