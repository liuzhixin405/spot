package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.SpotPendingDelegationCache;
import com.btc100.btc_q.exception.ServiceException;

public interface SpotPendingDelegationCacheService {

	String lock(String coinPairName, long id);

	void unLock(String coinPairName, long id, String value);

	int cancelPendingCache_one(SpotPendingDelegationCache cache, boolean needLog, BQSpotMarketCoinPair coinPair) throws ServiceException;

	SpotPendingDelegationCache getBuy1(String coinPairName);

	SpotPendingDelegationCache getSell1(String coinPairName);

	SpotPendingDelegationCache findPendingById(long orderId, String coinPairName);

	long insertCacheAndChangeAccount(SpotPendingDelegationCache cache, BigDecimal amount, BQAccount bqAccount,
			int currencyId, boolean needLog, int device, String ipAddr) throws ServiceException;

	List<SpotPendingDelegationCache> getAllByUserId(int direction, String coinPairName, Long uid);

	List<SpotPendingDelegationCache> findPendingAtPriceByUser(long uid, String coinPairName, int direction,
			BigDecimal price);

	Long getUnix_TIMESTAMP();

	List<SpotPendingDelegationCache> findPendingByUserIdAndTime(String coinPairName, long userId, long startTime, long endTime, int size, boolean sort);
	
	void insertCacheForDepth(List<SpotPendingDelegationCache> caches, long uid,  BigDecimal changeAccount, int direction, BQSpotMarketCoinPair  bqTransaction) throws ServiceException;
	
	List<SpotPendingDelegationCache> findBuyBelowPrice(String code, BigDecimal price, int size);
	
	List<SpotPendingDelegationCache> findSellAbovePrice(String code, BigDecimal price, int size);
	
	BigDecimal findSumBuyFrozen(String coinPairName, long userId);
	
	BigDecimal findSumSellFrozen(String coinPairName, long userId);
	
	///************************************ for read only **************************************/
	
	List<SpotPendingDelegationCache> readPendingByUserIdAndTime(String coinPairName, long userId, long startTime, long endTime, long from, String direct, int size);
	
	SpotPendingDelegationCache readPendingByPendingNo(String pendingNo, String coinPairName, long userId);
	
	List<SpotPendingDelegationCache> readPendingByUserIdAndSymbol(String coinPairName, long userId, String orderIds);
}
