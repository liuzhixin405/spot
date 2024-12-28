package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.HibernateException;

import com.btc100.btc_q.entity.BQAccountContract;
import com.btc100.btc_q.entity.PendingDelegationCache;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.vo.PagerModel;


public interface PendingDelegationCacheService {

	public void delete(String code,Long id);
	
	public String lock(String code, long id);
	
	public void unLock(String code, long id, String value);
	
	public int cancelPendingCache_one(PendingDelegationCache pd, BigDecimal coefficient, boolean needLog) throws ServiceException;

	/**
	 * 获取买1
	 */
	public PendingDelegationCache getBuy1(String code);
	public PendingDelegationCache getBuy1(String code,long f_user_id);
	/**
	 * 获取卖1
	 */
	public PendingDelegationCache getSell1(String code);
	public PendingDelegationCache getSell1(String code,long f_user_id);
	
	public PendingDelegationCache findPendingById(long orderId, String bqCode);
	
	public void insertCacheForDepth(List<PendingDelegationCache> caches, long uid,  BigDecimal changeAccount, String code) throws ServiceException;
	
	public long insertCacheAndChangeAccount(PendingDelegationCache cache, BigDecimal fee, BQAccountContract bqAccount, boolean needLog, int device, String ipAddr) throws ServiceException;

	public long insertPCK(PendingDelegationCache cahce,long mtid, BigDecimal coefficient, int device, String ipAddr) throws ServiceException;

	public long insertPCD(PendingDelegationCache cahce,long mtid, BigDecimal coefficient, int device, String ipAddr) throws ServiceException;
	
	List<PendingDelegationCache> getAllByUserId(int direction,String code,Long uid);
	
	public List<PendingDelegationCache> findPendingAtPriceByUser(long uid, String code, int direction, BigDecimal price);
	
	public List<PendingDelegationCache> findPendingByMemberTransInfo(String code, long userId, int priority, int direction, int multiple);
	
	public PendingDelegationCache saveCache(PendingDelegationCache cache) throws HibernateException;
	
	public List<PendingDelegationCache> queryPCDelegations(int direction, String code, Long fUserId, int multiple);

	public BigDecimal getSumByUIDAction(String tranCode,long uid,int direction,int priority);
	
	public List<PendingDelegationCache> findPendingByUserIdAndTime(String code, long userId, int priority, int direction, long statrtTime, long endTime, int start, int size, boolean sort);
	
	public List<PendingDelegationCache> findPendingByUserIdAndTime(String code, long userId, long startTime, long endTime, long from, String direct, int size);
	
	public Long getUnix_TIMESTAMP();
	
	public List<PendingDelegationCache> findBuyBelowPrice(String code, BigDecimal price, int size);
	
	public List<PendingDelegationCache> findSellAbovePrice(String code, BigDecimal price, int size);
	
	
	///************************************ for read only **************************************/
	
	public PendingDelegationCache readPendingByPendingNo(String pendingNo, String bqCode, long userId);
	
	public BigDecimal readSumByUIDAction(String tranCode,long uid,int direction,int priority);
	
	public List<PendingDelegationCache> readPendingByUserIdAndTime(String code, long userId, int priority, long statrtTime, long endTime, int size, boolean sort);
	
	public List<PendingDelegationCache> readPendingByUserIdAndTime(String code, long userId, long startTime, long endTime, long from, String direct, int size);
	
	public PagerModel readPagerModel(int direct, Long uid, String semail, String scode);
	
	PendingDelegationCache readOnePending(String bqCode, long userId, int multiple);
}
