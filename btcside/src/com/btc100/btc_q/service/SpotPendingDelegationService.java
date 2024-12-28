package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.HibernateException;

import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.SpotPendingDelegation;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.Top5DelegationVO;

public interface SpotPendingDelegationService {

	String lock(String coinPairName, long id);

	void unLock(String coinPairName, long id, String value);

	public int deletePending(String coinPairName, Long id) throws HibernateException;
	
	/**
	 * 获取买1
	 */
	public SpotPendingDelegation getBuy1(String coinPairName);

	/**
	 * 获取卖1
	 */
	public SpotPendingDelegation getSell1(String coinPairName);
	
	List<Top5DelegationVO> getBuyTop5(String coinPairName, long uid);

	List<Top5DelegationVO> getSellTop5(String coinPairName, long uid);
	
	List<Top5DelegationVO> getBuyTop5ExcludeUid(String coinPairName, long uid);

	List<Top5DelegationVO> getSellTop5ExcludeUid(String coinPairName, long uid);

	List<Top5DelegationVO> getBuyTop5(String coinPairName);

	List<Top5DelegationVO> getSellTop5(String coinPairName);

	List<Object[]> getBuyTopN(String coinPairName, int n);

	List<Object[]> getSellTopN(String coinPairName, int n);
	
	SpotPendingDelegation findPendingById(long orderId, String coinPairName);

	PagerModel getPagerModel(int direct, Long uid, String semail, String coinPairName);

	List<SpotPendingDelegation> getAllByUserId(int direction, String coinPairName, long fUserId);

	int cancelPending_one(SpotPendingDelegation pd, boolean needLog, BQSpotMarketCoinPair coinPair) throws ServiceException;

	BigDecimal getSumByAction(String coinPairName, int direction);
	
	BigDecimal getSumByUIDAction(String coinPairName, long uid, int direction);

	List<SpotPendingDelegation> findPendingBetweenPriceByUserId(long uid, String coinPairName, int direction,
			BigDecimal minPrice, BigDecimal maxPrice);

	List<SpotPendingDelegation> findPendingAtPriceByUser(long uid, String coinPairName, int direction,
			BigDecimal price);

	String getDepth(String coinPairName, int n);
	
	List<SpotPendingDelegation> findPendingByUserIdAndTime(String coinPairName, long userId, long startTime, long endTime, int start, int size, boolean sort);
	
	void insertPendingForDepth(List<SpotPendingDelegation> caches, long uid,  BigDecimal changeAccount, int direction, BQSpotMarketCoinPair  bqTransaction) throws ServiceException;
	
	BigDecimal findSumBuyFrozen(String coinPairName, long userId);
	
	BigDecimal findSumSellFrozen(String coinPairName, long userId); 
	
	///************************************ for read only **************************************/
	
	List<Top5DelegationVO> readBuyTop5(String coinPairName);
	
	List<Top5DelegationVO> readSellTop5(String coinPairName);
	
	List<Object[]> readBuyTopN(String coinPairName, int n);

	List<Object[]> readSellTopN(String coinPairName, int n);
	
	String readDepth(String coinPairName, int n);
	
	List<SpotPendingDelegation> readAllByUserId(String coinPairName, String direction, long fUserId, String orderby);

	List<SpotPendingDelegation> readPendingBetweenPrice(String coinPairName, int direction, BigDecimal minPrice,
			BigDecimal maxPrice);
	
	List<SpotPendingDelegation> readPendingByUserIdAndTime(String coinPairName, long userId, long startTime, long endTime, long from, String direct, int size);
	
	SpotPendingDelegation readPendingByPendingNo(String pendingNo, String coinPairName, long userId);
	
	List<SpotPendingDelegation> readPendingByUserIdAndSymbol(String coinPairName, long userId, String orderIds);
}
