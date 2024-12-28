package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.DeliveryPendingDelegation;
import com.btc100.btc_q.entity.DeliveryPendingDelegationCache;
import com.btc100.btc_q.entity.DeliveryTradeLog;
import com.btc100.btc_q.exception.ServiceException;

public interface DeliveryOpenPositionServer {

	List<DeliveryTradeLog> eatSell1(String code, DeliveryPendingDelegationCache buycache1,
			DeliveryPendingDelegation sell1, BigDecimal coefficient, boolean isQP) throws ServiceException;
	
	List<DeliveryTradeLog> eatSell1(String code, DeliveryPendingDelegation buy1,
			DeliveryPendingDelegation sell1, BigDecimal coefficient, boolean isQP) throws ServiceException;

	List<DeliveryTradeLog> eatBuyCache1(String code, DeliveryPendingDelegationCache buycache1,
			DeliveryPendingDelegation sell1, BigDecimal coefficient, boolean equal, boolean isQP)throws ServiceException;

	void moveBuyCache1(String code, DeliveryPendingDelegationCache buycache1, BigDecimal coefficient) throws ServiceException;

	List<DeliveryTradeLog> eatBuy1(String code, DeliveryPendingDelegation buy1,
			DeliveryPendingDelegationCache sellcache1, BigDecimal coefficient, boolean isQP)throws ServiceException ;
	
	List<DeliveryTradeLog> eatBuy1(String code, DeliveryPendingDelegation buy1,
			DeliveryPendingDelegation sell1, BigDecimal coefficient, boolean isQP)throws ServiceException ;

	List<DeliveryTradeLog> eatSellCache1(String code, DeliveryPendingDelegation buy1,
			DeliveryPendingDelegationCache sellcache1, BigDecimal coefficient, boolean equal, boolean isQP)throws ServiceException;

	void moveSellCache1(String code, DeliveryPendingDelegationCache sellcache1, BigDecimal coefficient) throws ServiceException;
	
	public void movePendingLog(DeliveryPendingDelegationCache delegationCache);
	
	public void movePendingLog(DeliveryPendingDelegation delegation);
}
