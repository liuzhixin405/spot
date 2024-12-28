package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.HibernateException;

import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.PendingDelegation;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.Top5DelegationVO;

public interface PendingDelegationService {

	public int delete(String code, Long id);
	
	public String lock(String code, long id);
	
	public void unLock(String code, long id, String value);

	/**
	 * 获取买1
	 */
	public PendingDelegation getBuy1(String code);

	public List<PendingDelegation> getBuyTop(String code, BigDecimal minPrice);
	public List<PendingDelegation> getSellTop(String code, BigDecimal maxPrice);

	/**
	 * 获取卖1
	 */
	public PendingDelegation getSell1(String code);

	public List<Top5DelegationVO> getBuyTop5(String transactionCode);

	public List<Top5DelegationVO> getSellTop5(String transactionCode);

	public List<Object[]> getBuyTopN(String bqCode, int n);

	public List<Object[]> getSellTopN(String bqCode, int n);
	
	public String getDepth(String bqCode, int n);

	public PendingDelegation findPendingById(long iorderId, String bqCode);

	public PagerModel getPagerModel(int direct, Long uid, String semail, String scode);
	
	/**
	 *查询用户所有合约的委托挂单
	 * @author gyl
	 */
	public List<PendingDelegation> getAllByUserId(List<BQTransaction> transactionList,String direction,String priority, long fUserId,String orderby);

	public List<PendingDelegation> getAllByUserId(int direction, String transactionCode, long fUserId);
	
	public int cancelPending_one(PendingDelegation pd, BigDecimal coefficient, boolean needLog) throws ServiceException;

	/**
	 * 计算委托爆仓未成交数量总和
	 * @param tranCode 合约号
	 * @param type 类型 用于区分挂单成交优先级(+:开仓, -:平仓, <>:爆仓)
	 * @param yesterdayTime 日期
	 * @return
	 */
	public BigDecimal findDelegationByCode(String tranCode, String type, long yesterdayTime);
	
	/**
	 * 通过方向和动作汇总
	 * @param tranCode 合约号
	 * @param direction 操作方向
	 * @param priority  挂单成交优先级(1:开仓, 2:平仓, 3:爆仓)
	 * @return
	 */
	public BigDecimal getSumByAction(String tranCode, int direction, int priority);
	
	BigDecimal getSumByUIDAction(String tranCode,long uid,int direction,int priority);

	public List<PendingDelegation> queryPCDelegations(int direction, String code, Long fUserId, int multiple);
	
	public int deletePending(String code, Long id) throws HibernateException;
	
	public List<PendingDelegation> findPendingBetweenPriceByUserId(long uid, String code, int direction, BigDecimal minPrice, BigDecimal maxPrice);
	
	public List<PendingDelegation> findPendingAtPriceByUser(long uid, String code, int direction, BigDecimal price);
	
	public List<PendingDelegation> findBCPendingOverOneMinute(String code, int direction);
	
	public List<PendingDelegation> findPendingByMemberTransInfo(String code, long userId, int priority, int direction, int multiple);
	
	public List<PendingDelegation> findPendingByUserIdAndTime(String code, long userId, int priority, int direction, long statrtTime, long endTime, int start, int size, boolean sort);
	
	List<PendingDelegation> findPendingByUserIdAndTime(String code, long userId, long startTime, long endTime, long from, String direct, int size);
	
	
	///************************************ for read only **************************************/
	
	public List<PendingDelegation> readBuyTop(String code, BigDecimal minPrice);
	
	public List<PendingDelegation> readSellTop(String code, BigDecimal maxPrice);
	
	public List<Object[]> readBuyTopN(String bqCode, int n);

	public List<Object[]> readSellTopN(String bqCode, int n);
	
	public String readDepth(String bqCode, int n);
	
	public PendingDelegation readPendingByPendingNo(String pendingNo, String bqCode, long userId);
	
	public BigDecimal readDelegation(Long id, String code, int i);
	
	public BigDecimal readDelegationDeposit(Long id, String code);
	
	/**
	 * 查询用户单个合约的委托挂单
	 * @author gyl
	 */
	public List<PendingDelegation> readAllByUserId(String transactionCode,String direction,String priority, long fUserId,String orderby);
	
	BigDecimal readSumByUIDAction(String tranCode,long uid,int direction,int priority);
	
	List<PendingDelegation> readPendingByUserIdAndTime(String code, long userId, long startTime, long endTime, long from, String direct, int size);
	
	PendingDelegation readOnePending(String bqCode, long userId, int multiple);
}
