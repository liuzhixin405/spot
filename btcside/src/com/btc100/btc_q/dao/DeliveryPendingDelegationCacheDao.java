package com.btc100.btc_q.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;

import com.btc100.btc_q.entity.DeliveryPendingDelegationCache;
import com.btc100.btc_q.vo.PagerModel;

public interface DeliveryPendingDelegationCacheDao<T> extends BaseDaoI<T> {
	
	public DeliveryPendingDelegationCache insertCache(DeliveryPendingDelegationCache cache) throws HibernateException;
	
	public boolean insertCache(final List<DeliveryPendingDelegationCache> caches, final String code) throws HibernateException;
	
	/**@deprecated*/
	public void delete();
	
	public int deletePending(String code, Long id)throws HibernateException;
	
	int deletePending(String code, int priority,int direction) throws HibernateException;
	
	public List<DeliveryPendingDelegationCache> query(String code,Map<String,Object> params)throws HibernateException;
	
	public DeliveryPendingDelegationCache getBuy1(String code);
	public DeliveryPendingDelegationCache getBuy1(String code,long f_user_id);
	public DeliveryPendingDelegationCache getSell1(String code);
	public DeliveryPendingDelegationCache getSell1(String code,long f_user_id);

	public void updateCacheDealNum(DeliveryPendingDelegationCache sellcache1);

	void updateCacheNumByID(long id,String scode,BigDecimal dealNum,BigDecimal undealNum,BigDecimal fromremain, BigDecimal frozenremain, BigDecimal coefficient);

	public List<DeliveryPendingDelegationCache> queryByUserId(int directionBuy, String code,
			long fUserId);

	public List<DeliveryPendingDelegationCache> getBuyList(String code0);

	public List<DeliveryPendingDelegationCache> getSellList(String code0);

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
	public int changePendingFlagForDelPC(int direction, String code, Long fUserId);
	
	/**
	 * 获取平仓单
	 * @param direction 方向-买单或者买单
	 * @param code	合约
	 * @param fUserId	用户
	 * @param multiple 杆杠
	 * @return 平仓单
	 */
	public List<DeliveryPendingDelegationCache> queryPCDelegations(int direction, String code, Long fUserId, int multiple);

	public int updateNumByID(long delegationId, String code,
			BigDecimal tradeCount, BigDecimal negate, BigDecimal zERO);
	
	public DeliveryPendingDelegationCache findPendingById(long iorderId, String bqCode, long userId);
	
	public DeliveryPendingDelegationCache findPendingById(long iorderId, String bqCode);
	
	public DeliveryPendingDelegationCache getPendingByPendingNo(String pendingNo, String bqCode, long userId);
	
	public List<DeliveryPendingDelegationCache> findPendingBetweenPrice(String code, int direction, BigDecimal minPrice, BigDecimal maxPrice);
	
	public List<DeliveryPendingDelegationCache> findPendingAtPriceByUser(long uid, String code, int direction, BigDecimal price);
	
	public List<DeliveryPendingDelegationCache> findPendingByMemberTransInfo(String code, long userId, int priority, int direction, int multiple);
	
	Object[] findSumDepositAndFeeByUser(String code, long userId);
	
	PagerModel getPagerModel(int iType, Long uid, String uemail, String code);
}
