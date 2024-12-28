package com.btc100.btc_q.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;

import com.btc100.btc_q.entity.PendingDelegationCache;
import com.btc100.btc_q.vo.PagerModel;

public interface PendingDelegationCacheDao<T> extends BaseDaoI<T> {
	
	public PendingDelegationCache insertCache(PendingDelegationCache cache) throws HibernateException;
	
	public boolean insertCache(final List<PendingDelegationCache> caches, final String code) throws HibernateException;
	
	/**@deprecated*/
	public void delete();
	
	public int deletePending(String code, Long id)throws HibernateException;
	
	int deletePending(String code, int priority,int direction) throws HibernateException;
	
	public List<PendingDelegationCache> query(String code,Map<String,Object> params)throws HibernateException;
	
	public PendingDelegationCache getBuy1(String code);
	public PendingDelegationCache getBuy1(String code,long f_user_id);
	public PendingDelegationCache getSell1(String code);
	public PendingDelegationCache getSell1(String code,long f_user_id);

	public void updateCacheDealNum(PendingDelegationCache sellcache1);

	void updateCacheNumByID(long id,String scode,BigDecimal dealNum,BigDecimal undealNum,BigDecimal fromremain, BigDecimal frozenremain, BigDecimal coefficient);

	public List<PendingDelegationCache> queryByUserId(int directionBuy, String code,
			long fUserId);

	public List<PendingDelegationCache> getBuyList(String code0);

	public List<PendingDelegationCache> getSellList(String code0);

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
	public List<PendingDelegationCache> queryPCDelegations(int direction, String code, Long fUserId, int multiple);

	public int updateNumByID(long delegationId, String code,
			BigDecimal tradeCount, BigDecimal negate, BigDecimal zERO);
	
	public PendingDelegationCache findPendingById(long iorderId, String bqCode, long userId);
	
	public PendingDelegationCache findPendingById(long iorderId, String bqCode);
	
	public PendingDelegationCache getPendingByPendingNo(String pendingNo, String bqCode, long userId);
	
	public List<PendingDelegationCache> findPendingBetweenPrice(String code, int direction, BigDecimal minPrice, BigDecimal maxPrice);
	
	public List<PendingDelegationCache> findPendingAtPriceByUser(long uid, String code, int direction, BigDecimal price);
	
	public List<PendingDelegationCache> findPendingByMemberTransInfo(String code, long userId, int priority, int direction, int multiple);
	
	Object[] findSumDepositAndFeeByUser(String code, long userId);
	
	PagerModel getPagerModel(int iType, Long uid, String uemail, String code);
}
