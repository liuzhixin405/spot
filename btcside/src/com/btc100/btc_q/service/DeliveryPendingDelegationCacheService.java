package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.HibernateException;

import com.btc100.btc_q.entity.DeliveryAccountContract;
import com.btc100.btc_q.entity.DeliveryPendingDelegationCache;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.vo.PagerModel;


public interface DeliveryPendingDelegationCacheService {

	public void delete(String code,Long id);
	
	public String lock(String code, long id);
	
	public void unLock(String code, long id, String value);
	
	public int cancelPendingCache_one(DeliveryPendingDelegationCache pd, BigDecimal coefficient, boolean needLog) throws ServiceException;

	/**
	 * 获取买1
	 */
	public DeliveryPendingDelegationCache getBuy1(String code);
	public DeliveryPendingDelegationCache getBuy1(String code,long f_user_id);
	/**
	 * 获取卖1
	 */
	public DeliveryPendingDelegationCache getSell1(String code);
	public DeliveryPendingDelegationCache getSell1(String code,long f_user_id);
	
	public DeliveryPendingDelegationCache findPendingById(long orderId, String bqCode);
	
	public long insertCacheAndChangeAccount(DeliveryPendingDelegationCache cache, BigDecimal fee, DeliveryAccountContract bqAccount, boolean needLog, int device, String ipAddr) throws ServiceException;

	public long insertPCK(DeliveryPendingDelegationCache cahce,long mtid, BigDecimal coefficient, int device, String ipAddr) throws ServiceException;

	public long insertPCD(DeliveryPendingDelegationCache cahce,long mtid, BigDecimal coefficient, int device, String ipAddr) throws ServiceException;
	
	List<DeliveryPendingDelegationCache> getAllByUserId(int direction,String code,Long uid);
	
	public List<DeliveryPendingDelegationCache> findPendingAtPriceByUser(long uid, String code, int direction, BigDecimal price);
	
	public List<DeliveryPendingDelegationCache> findPendingByMemberTransInfo(String code, long userId, int priority, int direction, int multiple);
	
	public DeliveryPendingDelegationCache saveCache(DeliveryPendingDelegationCache cache) throws HibernateException;
	
	public List<DeliveryPendingDelegationCache> queryPCDelegations(int direction, String code, Long fUserId, int multiple);

	public BigDecimal getSumByUIDAction(String tranCode,long uid,int direction,int priority);
	
	public List<DeliveryPendingDelegationCache> findPendingByUserIdAndTime(String code, long userId, int priority, int direction, long statrtTime, long endTime, int start, int size, boolean sort);
	
	public List<DeliveryPendingDelegationCache> findPendingByUserIdAndTime(String code, long userId, long startTime, long endTime, long from, String direct, int size);
	
	public Long getUnix_TIMESTAMP();
	
	public List<DeliveryPendingDelegationCache> findBuyBelowPrice(String code, BigDecimal price, int size);
	
	public List<DeliveryPendingDelegationCache> findSellAbovePrice(String code, BigDecimal price, int size);
	
	List<DeliveryPendingDelegationCache> findWithSize(String code, int size);
	
	public List<Integer> findUsers(String code, int n);
	
	public void insertCacheForDepth(List<DeliveryPendingDelegationCache> caches, long uid,  BigDecimal changeAccount, String code) throws ServiceException;
	
	///************************************ for read only **************************************/
	
	public DeliveryPendingDelegationCache readPendingByPendingNo(String pendingNo, String bqCode, long userId);
	
	public BigDecimal readSumByUIDAction(String tranCode,long uid,int direction,int priority);
	
	public List<DeliveryPendingDelegationCache> readPendingByUserIdAndTime(String code, long userId, int priority, long statrtTime, long endTime, int size, boolean sort);
	
	public List<DeliveryPendingDelegationCache> readPendingByUserIdAndTime(String code, long userId, long startTime, long endTime, long from, String direct, int size);
	
	public PagerModel readPagerModel(int direct, Long uid, String semail, String scode);
	
	DeliveryPendingDelegationCache readOnePending(String bqCode, long userId, int multiple);
}
