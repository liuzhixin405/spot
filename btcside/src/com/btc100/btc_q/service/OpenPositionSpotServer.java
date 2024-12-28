package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.SpotPendingDelegation;
import com.btc100.btc_q.entity.SpotPendingDelegationCache;
import com.btc100.btc_q.entity.SpotTradeLog;
import com.btc100.btc_q.exception.ServiceException;

public interface OpenPositionSpotServer {

	List<SpotTradeLog> eatSell1(BQSpotMarketCoinPair coinPair, SpotPendingDelegationCache buycache1,
			SpotPendingDelegation sell1) throws ServiceException;

	List<SpotTradeLog> eatBuyCache1(BQSpotMarketCoinPair coinPair, SpotPendingDelegationCache buycache1,
			SpotPendingDelegation sell1,boolean equal)throws ServiceException;

	List<SpotTradeLog> eatBuy1(BQSpotMarketCoinPair coinPair, SpotPendingDelegation buy1,
			SpotPendingDelegationCache sellcache1)throws ServiceException ;
	
	List<SpotTradeLog> eatSellCache1(BQSpotMarketCoinPair coinPair, SpotPendingDelegation buy1,
			SpotPendingDelegationCache sellcache1,boolean equal)throws ServiceException;

	void moveCache1(SpotPendingDelegationCache cache1) throws ServiceException;
	
	public void moveSpotPendingLog(SpotPendingDelegationCache delegationCache);
	public void moveSpotPendingLog(SpotPendingDelegation delegation);
}
