package com.btc100.btc_q.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;

import com.btc100.btc_q.entity.PendingDelegation;
import com.btc100.btc_q.entity.PendingDelegationCache;
import com.btc100.btc_q.vo.MarketPendingVO;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.Top5VO;

public interface PendingDelegationDao<T> extends BaseDaoI<T> {
	
	/**@deprecated**/
	public void delete();

	public int deletePending(String code, Long id) throws HibernateException;

	int deletePending(String code, int priority,int direction) throws HibernateException;
	
	public List<PendingDelegation> query(String code, Map<String, Object> params) throws HibernateException;

	public PendingDelegation getBuy1(String code);

	public PendingDelegation getSell1(String code);
	
	public List<PendingDelegation> getBuyTop(String code, BigDecimal minPrice);
	
	public List<PendingDelegation> getSellTop(String code, BigDecimal maxPrice);

	public PendingDelegation getBuy1(String code, long f_user_id);

	public PendingDelegation getSell1(String code, long f_user_id);

	public List<Top5VO> getBuyTop5(String transactionCode);

	public List<Top5VO> getSellTop5(String transactionCode);

	public List<PendingDelegation> queryByUserId(String code, long fUserId);
	
	List<com.btc100.btc_q.entity.PendingDelegation> queryByTypeUId(String code, String direction, String priority, long fUserId, String orderby);

	public List<PendingDelegation> queryByUserId(int direction, String code, long fUserId);

	public List<MarketPendingVO> getBuyList(String code0);

	public List<MarketPendingVO> getSellList(String code0);


	public List<Object[]> getBuyTopN(String bqCode, int n);

	public List<Object[]> getSellTopN(String bqCode, int n);


	public void updateDealNum(PendingDelegation sell1);
	
	int updateNumByID(long id,String scode,BigDecimal dealNum,BigDecimal undealNum, BigDecimal fromremain, BigDecimal frozenremain, BigDecimal coefficient);

	public int updateNumByID(long id, String scode, BigDecimal dealNum, BigDecimal deposit);
	
	public void moveFromCache(PendingDelegationCache buycache1);

	public PendingDelegation queryByUserIdOrderId(String bqCode, long userId, long orderId);

	// 分页
	public PagerModel getPagerModel(int iType, Long uid, String uemail, String code);

//	public boolean lock(String code, long id);
//	
//	public boolean unLock(String code, long id);
	
	BigDecimal getGain(String bqCode);
	BigDecimal getLoss(BigDecimal lastPrice,String bqCode,BigDecimal coefficient);

	/**
	 * 获取平仓单
	 * @param direction 方向-买单或者买单
	 * @param code	合约
	 * @param fUserId	用户
	 * @param multiple 杆杠
	 * @return 平仓单
	 */
	public List<PendingDelegation> queryPCDelegations(int direction, String code, Long fUserId, int multiple);
	
	public PendingDelegation findPendingById(long iorderId, String bqCode, long userId);
	
	public PendingDelegation findPendingById(long iorderId, String bqCode);
	
	public PendingDelegation getPendingByPendingNo(String pendingNo, String bqCode, long userId);
	
	public List<PendingDelegation> findPendingBetweenPrice(String code, int direction, BigDecimal minPrice, BigDecimal maxPrice);
	
	public List<PendingDelegation> findPendingBetweenPriceByUserId(long uid, String code, int direction, BigDecimal minPrice, BigDecimal maxPrice);
	
	public List<PendingDelegation> findPendingAtPriceByUser(long uid, String code, int direction, BigDecimal price);
	
	public List<PendingDelegation> findBCPendingOverOneMinute(String code, int direction);
	
	public List<PendingDelegation> findPendingByMemberTransInfo(String code, long userId, int priority, int direction, int multiple);
	
	Object[] findSumDepositAndFeeByUser(String code, long userId, BigDecimal coefficient);
}
