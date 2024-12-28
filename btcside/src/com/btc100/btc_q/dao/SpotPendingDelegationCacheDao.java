package com.btc100.btc_q.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.HibernateException;

import com.btc100.btc_q.entity.SpotPendingDelegationCache;

public interface SpotPendingDelegationCacheDao<T> extends BaseDaoI<T> {

	SpotPendingDelegationCache insertCache(SpotPendingDelegationCache cache) throws HibernateException;
	
	boolean insertCache(List<SpotPendingDelegationCache> caches, String coinPairName) throws HibernateException;

	int deletePending(String coinPairName, Long id) throws HibernateException;

	SpotPendingDelegationCache getBuy1(String coinPairName);

	SpotPendingDelegationCache getBuy1(String coinPairName, long fUserId);

	SpotPendingDelegationCache getSell1(String coinPairName);

	SpotPendingDelegationCache getSell1(String coinPairName, long fUserId);

	List<SpotPendingDelegationCache> queryByUserId(int direction, String coinPairName, long fUserId);

//	boolean lock(String coinPairName, long id);
//
//	boolean unLock(String coinPairName, long id);

	int updateNumByID(long id, String coinPairName, BigDecimal dealNum, BigDecimal undealNum);

	SpotPendingDelegationCache findPendingById(long orderId, String coinPairName);

	List<SpotPendingDelegationCache> findPendingAtPriceByUser(long uid, String coinPairName, int direction, BigDecimal price);

	List<SpotPendingDelegationCache> findPendingByMemberTransInfo(String coinPairName, long uId, int direction);

	BigDecimal findSumBuyFrozen(String coinPairName, long userId);
	
	BigDecimal findSumSellFrozen(String coinPairName, long userId);
	
	SpotPendingDelegationCache getPendingByPendingNo(String pendingNo, String coinPairName, long userId);
	
}
