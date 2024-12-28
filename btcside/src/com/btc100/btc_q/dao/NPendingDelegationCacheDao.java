package com.btc100.btc_q.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;

import com.btc100.btc_q.entity.NPendingDelegationCache;
import com.btc100.btc_q.vo.PagerModel;

public interface NPendingDelegationCacheDao<T> extends BaseDaoI<T> {
	
	public NPendingDelegationCache insertCache(String partition, NPendingDelegationCache cache) throws HibernateException;
	
	public boolean insertCache(String partition, final List<NPendingDelegationCache> caches, final String code) throws HibernateException;
	
	public int deletePending(String partition, String code, Long id)throws HibernateException;
	
	int deletePending(String partition, String code, int priority,int direction) throws HibernateException;
	
	public List<NPendingDelegationCache> query(String partition, String code,Map<String,Object> params)throws HibernateException;
	
	public NPendingDelegationCache getBuy1(String partition, String code);
	public NPendingDelegationCache getBuy1(String partition, String code,long f_user_id);
	public NPendingDelegationCache getSell1(String partition, String code);
	public NPendingDelegationCache getSell1(String partition, String code,long f_user_id);

	public void updateCacheDealNum(String partition, NPendingDelegationCache sellcache1);

	void updateCacheNumByID(String partition, long id,String scode,BigDecimal dealNum,BigDecimal undealNum,BigDecimal fromremain, BigDecimal frozenremain, BigDecimal coefficient);

	public List<NPendingDelegationCache> queryByUserId(String partition, int directionBuy, String code,
			long fUserId);

	public List<NPendingDelegationCache> getBuyList(String partition, String code0);

	public List<NPendingDelegationCache> getSellList(String partition, String code0);

//	public boolean lock(String code, long id);
//	
//	public boolean unLock(String code, long id);

	/**
	 * 删除平仓挂单前，改变挂单的flag，表明此单即将被撤销
	 * @param direction 方向-买单或者买单
	 * @param code	合约
	 * @param fUserId	用户
	 * @return 成功>0 失败=0
	 */
	public int changePendingFlagForDelPC(String partition, int direction, String code, Long fUserId);
	
	/**
	 * 获取平仓单
	 * @param direction 方向-买单或者买单
	 * @param code	合约
	 * @param fUserId	用户
	 * @param multiple 杆杠
	 * @return 平仓单
	 */
	public List<NPendingDelegationCache> queryPCDelegations(String partition, int direction, String code, Long fUserId, int multiple);

	public int updateNumByID(String partition, long delegationId, String code,
			BigDecimal tradeCount, BigDecimal negate, BigDecimal zERO);
	
	public NPendingDelegationCache findPendingById(String partition, long iorderId, String bqCode, long userId);
	
	public NPendingDelegationCache findPendingById(String partition, long iorderId, String bqCode);
	
	public NPendingDelegationCache getPendingByPendingNo(String partition, String pendingNo, String bqCode, long userId);
	
	public List<NPendingDelegationCache> findPendingBetweenPrice(String partition, String code, int direction, BigDecimal minPrice, BigDecimal maxPrice);
	
	public List<NPendingDelegationCache> findPendingAtPriceByUser(String partition, long uid, String code, int direction, BigDecimal price);
	
	public List<NPendingDelegationCache> findPendingByMemberTransInfo(String partition, String code, long userId, int priority, int direction, int multiple);
	
	Object[] findSumDepositAndFeeByUser(String partition, String code, long userId);
	
	PagerModel getPagerModel(String partition, int iType, Long uid, String uemail, String code);
}
