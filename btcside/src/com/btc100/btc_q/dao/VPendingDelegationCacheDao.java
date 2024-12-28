package com.btc100.btc_q.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;

import com.btc100.btc_q.entity.VPendingDelegationCache;

public interface VPendingDelegationCacheDao<T> extends BaseDaoI<T> {
	
	public VPendingDelegationCache insertCache(VPendingDelegationCache cache) throws HibernateException;
	
	public boolean insertCache(final List<VPendingDelegationCache> caches, final String code) throws HibernateException;
	
	/**@deprecated*/
	public void delete();
	
	public int deletePending(String code, Long id)throws HibernateException;
	
	int deletePending(String code, int priority,int direction) throws HibernateException;
	
	public List<VPendingDelegationCache> query(String code,Map<String,Object> params)throws HibernateException;
	
	public VPendingDelegationCache getBuy1(String code);
	public VPendingDelegationCache getBuy1(String code,long f_user_id);
	public VPendingDelegationCache getSell1(String code);
	public VPendingDelegationCache getSell1(String code,long f_user_id);

	public void updateCacheDealNum(VPendingDelegationCache sellcache1);

	void updateCacheNumByID(long id,String scode,BigDecimal dealNum,BigDecimal undealNum,BigDecimal fromremain, BigDecimal frozenremain);

	public List<VPendingDelegationCache> queryByUserId(int directionBuy, String code,
			long fUserId);

	public List<VPendingDelegationCache> getBuyList(String code0);

	public List<VPendingDelegationCache> getSellList(String code0);

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
	public List<VPendingDelegationCache> queryPCDelegations(int direction, String code, Long fUserId, int multiple);

	public int updateNumByID(long delegationId, String code,
			BigDecimal tradeCount, BigDecimal negate, BigDecimal zERO);
	
	public VPendingDelegationCache findPendingById(long iorderId, String vCode, long userId);
	
	public VPendingDelegationCache findPendingById(long iorderId, String vCode);
	
	public VPendingDelegationCache getPendingByPendingNo(String pendingNo, String vCode, long userId);
	
	public List<VPendingDelegationCache> findPendingBetweenPrice(String code, int direction, BigDecimal minPrice, BigDecimal maxPrice);
	
	public List<VPendingDelegationCache> findPendingAtPriceByUser(long uid, String code, int direction, BigDecimal price);
	
	public List<VPendingDelegationCache> findPendingByMemberTransInfo(String code, long userId, int priority, int direction, int multiple);
	
	Object[] findSumDepositAndFeeByUser(String code, long userId);
}
