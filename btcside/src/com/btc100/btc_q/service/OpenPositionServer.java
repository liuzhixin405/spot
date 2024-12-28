package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.PendingDelegation;
import com.btc100.btc_q.entity.PendingDelegationCache;
import com.btc100.btc_q.entity.TradeLog;
import com.btc100.btc_q.exception.ServiceException;

public interface OpenPositionServer {

	List<TradeLog> eatSell1(String code, PendingDelegationCache buycache1,
			PendingDelegation sell1, BigDecimal coefficient, boolean isQP) throws ServiceException;
	
	List<TradeLog> eatSell1(String code, PendingDelegation buy1,
			PendingDelegation sell1, BigDecimal coefficient, boolean isQP) throws ServiceException;

	List<TradeLog> eatBuyCache1(String code, PendingDelegationCache buycache1,
			PendingDelegation sell1, BigDecimal coefficient, boolean equal, boolean isQP)throws ServiceException;

	void moveBuyCache1(String code, PendingDelegationCache buycache1, BigDecimal coefficient) throws ServiceException;

	List<TradeLog> eatBuy1(String code, PendingDelegation buy1,
			PendingDelegationCache sellcache1, BigDecimal coefficient, boolean isQP)throws ServiceException ;
	
	List<TradeLog> eatBuy1(String code, PendingDelegation buy1,
			PendingDelegation sell1, BigDecimal coefficient, boolean isQP)throws ServiceException ;

	List<TradeLog> eatSellCache1(String code, PendingDelegation buy1,
			PendingDelegationCache sellcache1, BigDecimal coefficient, boolean equal, boolean isQP)throws ServiceException;

	void moveSellCache1(String code, PendingDelegationCache sellcache1, BigDecimal coefficient) throws ServiceException;
	
	public void movePendingLog(PendingDelegationCache delegationCache);
	
	public void movePendingLog(PendingDelegation delegation);
}
