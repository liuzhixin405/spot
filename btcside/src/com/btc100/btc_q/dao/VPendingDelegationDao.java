package com.btc100.btc_q.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;

import com.btc100.btc_q.entity.VPendingDelegation;
import com.btc100.btc_q.entity.VPendingDelegationCache;
import com.btc100.btc_q.vo.MarketPendingVO;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.Top5VO;

public interface VPendingDelegationDao<T> extends BaseDaoI<T> {
	
	/**@deprecated**/
	public void delete();

	public int deletePending(String code, Long id) throws HibernateException;

	int deletePending(String code, int priority,int direction) throws HibernateException;
	
	public List<VPendingDelegation> query(String code, Map<String, Object> params) throws HibernateException;

	public VPendingDelegation getBuy1(String code);

	public VPendingDelegation getSell1(String code);
	
	public List<VPendingDelegation> getBuyTop(String code, BigDecimal minPrice);
	
	public List<VPendingDelegation> getSellTop(String code, BigDecimal maxPrice);

	public VPendingDelegation getBuy1(String code, long f_user_id);

	public VPendingDelegation getSell1(String code, long f_user_id);

	public List<Top5VO> getBuyTop5(String transactionCode);

	public List<Top5VO> getSellTop5(String transactionCode);

	public List<VPendingDelegation> queryByUserId(String code, long fUserId);
	
	List<com.btc100.btc_q.entity.VPendingDelegation> queryByTypeUId(String code, String direction, String priority, long fUserId, String orderby);

	public List<VPendingDelegation> queryByUserId(int direction, String code, long fUserId);

	public List<MarketPendingVO> getBuyList(String code0);

	public List<MarketPendingVO> getSellList(String code0);


	public List<Object[]> getBuyTopN(String vCode, int n);

	public List<Object[]> getSellTopN(String vCode, int n);


	public void updateDealNum(VPendingDelegation sell1);
	
	int updateNumByID(long id,String scode,BigDecimal dealNum,BigDecimal undealNum, BigDecimal fromremain, BigDecimal frozenremain);

	public int updateNumByID(long id, String scode, BigDecimal dealNum, BigDecimal deposit);
	
	public void moveFromCache(VPendingDelegationCache buycache1);

	public VPendingDelegation queryByUserIdOrderId(String vCode, long userId, long orderId);

	// 分页
	public PagerModel getPagerModel(int iType, Long uid, String uemail, String code);

//	public boolean lock(String code, long id);
//	
//	public boolean unLock(String code, long id);
	
	BigDecimal getGain(String vCode);
	BigDecimal getLoss(BigDecimal lastPrice,String vCode,BigDecimal coefficient);

	/**
	 * 获取平仓单
	 * @param direction 方向-买单或者买单
	 * @param code	合约
	 * @param fUserId	用户
	 * @param multiple 杆杠
	 * @return 平仓单
	 */
	public List<VPendingDelegation> queryPCDelegations(int direction, String code, Long fUserId, int multiple);
	
	public VPendingDelegation findPendingById(long iorderId, String vCode, long userId);
	
	public VPendingDelegation findPendingById(long iorderId, String vCode);
	
	public VPendingDelegation getPendingByPendingNo(String pendingNo, String bqCode, long userId);
	
	public List<VPendingDelegation> findPendingBetweenPrice(String code, int direction, BigDecimal minPrice, BigDecimal maxPrice);
	
	public List<VPendingDelegation> findPendingBetweenPriceByUserId(long uid, String code, int direction, BigDecimal minPrice, BigDecimal maxPrice);
	
	public List<VPendingDelegation> findPendingAtPriceByUser(long uid, String code, int direction, BigDecimal price);
	
	public List<VPendingDelegation> findBCPendingOverOneMinute(String code, int direction);
	
	public List<VPendingDelegation> findPendingByMemberTransInfo(String code, long userId, int priority, int direction, int multiple);
	
	Object[] findSumDepositAndFeeByUser(String code, long userId);
}
