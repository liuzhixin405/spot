package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.HibernateException;

import com.btc100.btc_q.entity.NAccountContract;
import com.btc100.btc_q.entity.NPendingDelegationCache;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.vo.PagerModel;


public interface NPendingDelegationCacheService {

	public void delete(String partition, String code,Long id);
	
	public String lock(String partition, String code, long id);
	
	public void unLock(String partition, String code, long id, String value);
	
	public int cancelPendingCache_one(String partition, int currencyId, NPendingDelegationCache pd, BigDecimal coefficient, boolean needLog) throws ServiceException;

	/**
	 * 获取买1
	 */
	public NPendingDelegationCache getBuy1(String partition, String code);
	public NPendingDelegationCache getBuy1(String partition, String code,long f_user_id);
	/**
	 * 获取卖1
	 */
	public NPendingDelegationCache getSell1(String partition, String code);
	public NPendingDelegationCache getSell1(String partition, String code,long f_user_id);
	
	public NPendingDelegationCache findPendingById(String partition, long orderId, String bqCode);
	
	public void insertCacheForDepth(String partition, List<NPendingDelegationCache> caches, long uid,  BigDecimal changeAccount, String code) throws ServiceException;
	
	public long insertCacheAndChangeAccount(String partition, int currencyId, NPendingDelegationCache cache, BigDecimal fee, NAccountContract bqAccount, boolean needLog, int device, String ipAddr) throws ServiceException;

	public long insertPCK(String partition, NPendingDelegationCache cahce,long mtid, BigDecimal coefficient, int device, String ipAddr) throws ServiceException;

	public long insertPCD(String partition, NPendingDelegationCache cahce,long mtid, BigDecimal coefficient, int device, String ipAddr) throws ServiceException;
	
	List<NPendingDelegationCache> getAllByUserId(String partition, int direction,String code,Long uid);
	
	public List<NPendingDelegationCache> findPendingAtPriceByUser(String partition, long uid, String code, int direction, BigDecimal price);
	
	public List<NPendingDelegationCache> findPendingByMemberTransInfo(String partition, String code, long userId, int priority, int direction, int multiple);
	
	public NPendingDelegationCache saveCache(String partition, NPendingDelegationCache cache) throws HibernateException;
	
	public List<NPendingDelegationCache> queryPCDelegations(String partition, int direction, String code, Long fUserId, int multiple);

	public BigDecimal getSumByUIDAction(String partition, String tranCode,long uid,int direction,int priority);
	
	public List<NPendingDelegationCache> findPendingByUserIdAndTime(String partition, String code, long userId, int priority, int direction, long statrtTime, long endTime, int start, int size, boolean sort);
	
	public List<NPendingDelegationCache> findPendingByUserIdAndTime(String partition, String code, long userId, long startTime, long endTime, long from, String direct, int size);
	
	public Long getUnix_TIMESTAMP();
	
	public List<NPendingDelegationCache> findBuyBelowPrice(String partition, String code, BigDecimal price, int size);
	
	public List<NPendingDelegationCache> findSellAbovePrice(String partition, String code, BigDecimal price, int size);
	
	
	///************************************ for read only **************************************/
	
	public NPendingDelegationCache readPendingByPendingNo(String partition, String pendingNo, String bqCode, long userId);
	
	public BigDecimal readSumByUIDAction(String partition, String tranCode,long uid,int direction,int priority);
	
	public List<NPendingDelegationCache> readPendingByUserIdAndTime(String partition, String code, long userId, int priority, long statrtTime, long endTime, int size, boolean sort);
	
	public List<NPendingDelegationCache> readPendingByUserIdAndTime(String partition, String code, long userId, long startTime, long endTime, long from, String direct, int size);
	
	public PagerModel readPagerModel(String partition, int direct, Long uid, String semail, String scode);
	
	NPendingDelegationCache readOnePending(String partition, String bqCode, long userId, int multiple);
	
	public List<NPendingDelegationCache>readPendingByUserIdAndSymbol(String partition, String bqCode, long userId, String orderIds);
}
