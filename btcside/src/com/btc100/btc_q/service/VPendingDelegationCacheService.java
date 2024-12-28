package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.HibernateException;

import com.btc100.btc_q.entity.VAccountContract;
import com.btc100.btc_q.entity.VPendingDelegationCache;
import com.btc100.btc_q.exception.ServiceException;


public interface VPendingDelegationCacheService {

	public void delete(String code,Long id);
	
	public String lock(String code, long id);
	
	public void unLock(String code, long id, String value);
	
	public int cancelPendingCache_one(VPendingDelegationCache pd, int currencyId, boolean needLog) throws ServiceException;

	/**
	 * 获取买1
	 */
	public VPendingDelegationCache getBuy1(String code);
	/**
	 * 获取卖1
	 */
	public VPendingDelegationCache getSell1(String code);
	
	public VPendingDelegationCache findPendingById(long orderId, String vCode);
	
	public long insertCacheAndChangeAccount(VPendingDelegationCache cache, int currencyId, BigDecimal fee, VAccountContract vAccount, 
			boolean needLog, int device, String ipAddr) throws ServiceException;

	public long insertPCK(VPendingDelegationCache cahce,long mtid, int device, String ipAddr) throws ServiceException;

	public long insertPCD(VPendingDelegationCache cahce,long mtid, int device, String ipAddr) throws ServiceException;
	
	List<VPendingDelegationCache> getAllByUserId(int direction,String code,Long uid);
	
	public List<VPendingDelegationCache> findPendingAtPriceByUser(long uid, String code, int direction, BigDecimal price);
	
	public List<VPendingDelegationCache> findPendingByMemberTransInfo(String code, long userId, int priority, int direction, int multiple);
	
	public VPendingDelegationCache saveCache(VPendingDelegationCache cache) throws HibernateException;
	
	public List<VPendingDelegationCache> queryPCDelegations(int direction, String code, Long fUserId, int multiple);
	
	public List<VPendingDelegationCache> findBuyBelowPrice(String code, BigDecimal price, int size);
	
	public List<VPendingDelegationCache> findSellAbovePrice(String code, BigDecimal price, int size);
	
	public void insertCacheForDepth(List<VPendingDelegationCache> caches, long uid,  BigDecimal changeAccount, String code) throws ServiceException;
	
	
	///************************************ for read only **************************************/
	
	public VPendingDelegationCache readPendingByPendingNo(String pendingNo, String bqCode, long userId);

	public List<VPendingDelegationCache> readPendingByUserIdAndTime(String code, long userId, long statrtTime, long endTime, int size);
	
	List<VPendingDelegationCache> readPendingByUserIdAndTime(String code, long userId, long startTime, long endTime, long from, String direct, int size);

	VPendingDelegationCache readOnePending(String bqCode, long userId, int multiple);
}
