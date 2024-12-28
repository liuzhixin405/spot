package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.NPendingDelegation;
import com.btc100.btc_q.entity.NPendingDelegationCache;
import com.btc100.btc_q.entity.NTradeLog;
import com.btc100.btc_q.exception.ServiceException;

public interface NOpenPositionServer {

	List<NTradeLog> eatSell1(int currencyId, String partition, String code, NPendingDelegationCache buycache1,
			NPendingDelegation sell1, BigDecimal coefficient, boolean isQP) throws ServiceException;
	
	List<NTradeLog> eatSell1(int currencyId, String partition, String code, NPendingDelegation buy1,
			NPendingDelegation sell1, BigDecimal coefficient, boolean isQP) throws ServiceException;

	List<NTradeLog> eatBuyCache1(int currencyId, String partition, String code, NPendingDelegationCache buycache1,
			NPendingDelegation sell1, BigDecimal coefficient, boolean equal, boolean isQP)throws ServiceException;

	void moveBuyCache1(int currencyId, String partition, String code, NPendingDelegationCache buycache1, BigDecimal coefficient) throws ServiceException;

	List<NTradeLog> eatBuy1(int currencyId, String partition, String code, NPendingDelegation buy1,
			NPendingDelegationCache sellcache1, BigDecimal coefficient, boolean isQP)throws ServiceException ;
	
	List<NTradeLog> eatBuy1(int currencyId, String partition, String code, NPendingDelegation buy1,
			NPendingDelegation sell1, BigDecimal coefficient, boolean isQP)throws ServiceException ;

	List<NTradeLog> eatSellCache1(int currencyId, String partition, String code, NPendingDelegation buy1,
			NPendingDelegationCache sellcache1, BigDecimal coefficient, boolean equal, boolean isQP)throws ServiceException;

	void moveSellCache1(int currencyId, String partition, String code, NPendingDelegationCache sellcache1, BigDecimal coefficient) throws ServiceException;
	
	public void movePendingLog(String partition, NPendingDelegationCache delegationCache);
	
	public void movePendingLog(String partition, NPendingDelegation delegation);
}
