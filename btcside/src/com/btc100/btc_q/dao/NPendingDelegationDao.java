package com.btc100.btc_q.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;

import com.btc100.btc_q.entity.NPendingDelegation;
import com.btc100.btc_q.entity.NPendingDelegationCache;
import com.btc100.btc_q.vo.MarketPendingVO;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.Top5VO;

public interface NPendingDelegationDao<T> extends BaseDaoI<T> {

	public int deletePending(String partition, String code, Long id) throws HibernateException;

	int deletePending(String partition, String code, int priority,int direction) throws HibernateException;
	
	public List<NPendingDelegation> query(String partition, String code, Map<String, Object> params) throws HibernateException;

	public NPendingDelegation getBuy1(String partition, String code);

	public NPendingDelegation getSell1(String partition, String code);
	
	public List<NPendingDelegation> getBuyTop(String partition, String code, BigDecimal minPrice);
	
	public List<NPendingDelegation> getSellTop(String partition, String code, BigDecimal maxPrice);

	public NPendingDelegation getBuy1(String partition, String code, long f_user_id);

	public NPendingDelegation getSell1(String partition, String code, long f_user_id);

	public List<Top5VO> getBuyTop5(String partition, String transactionCode);

	public List<Top5VO> getSellTop5(String partition, String transactionCode);

	public List<NPendingDelegation> queryByUserId(String partition, String code, long fUserId);
	
	List<com.btc100.btc_q.entity.NPendingDelegation> queryByTypeUId(String partition, String code, String direction, String priority, long fUserId, String orderby);

	public List<NPendingDelegation> queryByUserId(String partition, int direction, String code, long fUserId);

	public List<MarketPendingVO> getBuyList(String partition, String code0);

	public List<MarketPendingVO> getSellList(String partition, String code0);


	public List<Object[]> getBuyTopN(String partition, String bqCode, int n);

	public List<Object[]> getSellTopN(String partition, String bqCode, int n);


	public void updateDealNum(String partition, NPendingDelegation sell1);
	
	int updateNumByID(String partition, long id,String scode,BigDecimal dealNum,BigDecimal undealNum, BigDecimal fromremain, BigDecimal frozenremain, BigDecimal coefficient);

	public int updateNumByID(String partition, long id, String scode, BigDecimal dealNum, BigDecimal deposit);
	
	public void moveFromCache(String partition, NPendingDelegationCache buycache1);

	public NPendingDelegation queryByUserIdOrderId(String partition, String bqCode, long userId, long orderId);

	// 分页
	public PagerModel getPagerModel(String partition, int iType, Long uid, String uemail, String code);

//	public boolean lock(String code, long id);
//	
//	public boolean unLock(String code, long id);
	
	BigDecimal getGain(String partition, String bqCode);
	BigDecimal getLoss(String partition, BigDecimal lastPrice,String bqCode,BigDecimal coefficient);

	/**
	 * 获取平仓单
	 * @param direction 方向-买单或者买单
	 * @param code	合约
	 * @param fUserId	用户
	 * @param multiple 杆杠
	 * @return 平仓单
	 */
	public List<NPendingDelegation> queryPCDelegations(String partition, int direction, String code, Long fUserId, int multiple);
	
	public NPendingDelegation findPendingById(String partition, long iorderId, String bqCode, long userId);
	
	public NPendingDelegation findPendingById(String partition, long iorderId, String bqCode);
	
	public NPendingDelegation getPendingByPendingNo(String partition, String pendingNo, String bqCode, long userId);
	
	public List<NPendingDelegation> findPendingBetweenPrice(String partition, String code, int direction, BigDecimal minPrice, BigDecimal maxPrice);
	
	public List<NPendingDelegation> findPendingBetweenPriceByUserId(String partition, long uid, String code, int direction, BigDecimal minPrice, BigDecimal maxPrice);
	
	public List<NPendingDelegation> findPendingAtPriceByUser(String partition, long uid, String code, int direction, BigDecimal price);
	
	public List<NPendingDelegation> findBCPendingOverOneMinute(String partition, String code, int direction);
	
	public List<NPendingDelegation> findPendingByMemberTransInfo(String partition, String code, long userId, int priority, int direction, int multiple);
	
	Object[] findSumDepositAndFeeByUser(String partition, String code, long userId, BigDecimal coefficient);
}
