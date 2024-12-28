package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.VPendingDelegation;
import com.btc100.btc_q.entity.VPendingDelegationCache;
import com.btc100.btc_q.entity.VTradeLog;
import com.btc100.btc_q.exception.ServiceException;

public interface VOpenPositionServer {

	List<VTradeLog> eatSell1(String code, int currencyId, VPendingDelegationCache buycache1,
			VPendingDelegation sell1, boolean isQP) throws ServiceException;
	
	List<VTradeLog> eatSell1(String code, int currencyId, VPendingDelegation buy1,
			VPendingDelegation sell1, boolean isQP) throws ServiceException;

	List<VTradeLog> eatBuyCache1(String code, int currencyId, VPendingDelegationCache buycache1,
			VPendingDelegation sell1,boolean equal, boolean isQP)throws ServiceException;

	void moveBuyCache1(String code, int currencyId, VPendingDelegationCache buycache1) throws ServiceException;

	List<VTradeLog> eatBuy1(String code, int currencyId, VPendingDelegation buy1,
			VPendingDelegationCache sellcache1, boolean isQP)throws ServiceException ;
	
	List<VTradeLog> eatBuy1(String code, int currencyId, VPendingDelegation buy1,
			VPendingDelegation sell1, boolean isQP)throws ServiceException ;

	List<VTradeLog> eatSellCache1(String code, int currencyId, VPendingDelegation buy1,
			VPendingDelegationCache sellcache1,boolean equal, boolean isQP)throws ServiceException;
	
	void adjustSellCacheFrozen(VPendingDelegation buy, VPendingDelegationCache sellCache, int currencyId) throws ServiceException;

	void moveSellCache1(String code, int currencyId, VPendingDelegationCache sellcache1) throws ServiceException;
	
	public void movePendingLog(VPendingDelegationCache delegationCache);
	
	public void movePendingLog(VPendingDelegation delegation);
}
