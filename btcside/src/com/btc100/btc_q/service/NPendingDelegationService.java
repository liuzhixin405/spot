package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.HibernateException;

import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.entity.NPendingDelegation;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.Top5DelegationVO;

public interface NPendingDelegationService {

	public int delete(String partition, String code, Long id);
	
	public String lock(String partition, String code, long id);
	
	public void unLock(String partition, String code, long id, String value);

	/**
	 * 获取买1
	 */
	public NPendingDelegation getBuy1(String partition, String code);

	public List<NPendingDelegation> getBuyTop(String partition, String code, BigDecimal minPrice);
	public List<NPendingDelegation> getSellTop(String partition, String code, BigDecimal maxPrice);

	/**
	 * 获取卖1
	 */
	public NPendingDelegation getSell1(String partition, String code);

	public List<Top5DelegationVO> getBuyTop5(String partition, String transactionCode);

	public List<Top5DelegationVO> getSellTop5(String partition, String transactionCode);

	public List<Object[]> getBuyTopN(String partition, String bqCode, int n);

	public List<Object[]> getSellTopN(String partition, String bqCode, int n);
	
	public String getDepth(String partition, String bqCode, int n);

	public NPendingDelegation findPendingById(String partition, long iorderId, String bqCode);

	public PagerModel getPagerModel(String partition, int direct, Long uid, String semail, String scode);
	
	/**
	 *查询用户所有合约的委托挂单
	 * @author gyl
	 */
	public List<NPendingDelegation> getAllByUserId(String partition, List<NTransaction> transactionList,String direction,String priority, long fUserId,String orderby);

	public List<NPendingDelegation> getAllByUserId(String partition, int direction, String transactionCode, long fUserId);
	
	public int cancelPending_one(String partition, int currencyId, NPendingDelegation pd, BigDecimal coefficient, boolean needLog) throws ServiceException;

	/**
	 * 计算委托爆仓未成交数量总和
	 * @param tranCode 合约号
	 * @param type 类型 用于区分挂单成交优先级(+:开仓, -:平仓, <>:爆仓)
	 * @param yesterdayTime 日期
	 * @return
	 */
	public BigDecimal findDelegationByCode(String partition, String tranCode, String type, long yesterdayTime);
	
	/**
	 * 通过方向和动作汇总
	 * @param tranCode 合约号
	 * @param direction 操作方向
	 * @param priority  挂单成交优先级(1:开仓, 2:平仓, 3:爆仓)
	 * @return
	 */
	public BigDecimal getSumByAction(String partition, String tranCode, int direction, int priority);
	
	BigDecimal getSumByUIDAction(String partition, String tranCode,long uid,int direction,int priority);

	public List<NPendingDelegation> queryPCDelegations(String partition, int direction, String code, Long fUserId, int multiple);
	
	public int deletePending(String partition, String code, Long id) throws HibernateException;
	
	public List<NPendingDelegation> findPendingBetweenPriceByUserId(String partition, long uid, String code, int direction, BigDecimal minPrice, BigDecimal maxPrice);
	
	public List<NPendingDelegation> findPendingAtPriceByUser(String partition, long uid, String code, int direction, BigDecimal price);
	
	public List<NPendingDelegation> findBCPendingOverOneMinute(String partition, String code, int direction);
	
	public List<NPendingDelegation> findPendingByMemberTransInfo(String partition, String code, long userId, int priority, int direction, int multiple);
	
	public List<NPendingDelegation> findPendingByUserIdAndTime(String partition, String code, long userId, int priority, int direction, long statrtTime, long endTime, int start, int size, boolean sort);
	
	List<NPendingDelegation> findPendingByUserIdAndTime(String partition, String code, long userId, long startTime, long endTime, long from, String direct, int size);
	
	
	///************************************ for read only **************************************/
	
	public List<NPendingDelegation> readBuyTop(String partition, String code, BigDecimal minPrice);
	
	public List<NPendingDelegation> readSellTop(String partition, String code, BigDecimal maxPrice);
	
	public List<Object[]> readBuyTopN(String partition, String bqCode, int n);

	public List<Object[]> readSellTopN(String partition, String bqCode, int n);
	
	public String readDepth(String partition, String bqCode, int n);
	
	public NPendingDelegation readPendingByPendingNo(String partition, String pendingNo, String bqCode, long userId);
	
	public BigDecimal readDelegation(String partition, Long id, String code, int i);
	
	public BigDecimal readDelegationDeposit(String partition, Long id, String code);
	
	/**
	 * 查询用户单个合约的委托挂单
	 * @author gyl
	 */
	public List<NPendingDelegation> readAllByUserId(String partition, String transactionCode,String direction,String priority, long fUserId,String orderby);
	
	BigDecimal readSumByUIDAction(String partition, String tranCode,long uid,int direction,int priority);
	
	List<NPendingDelegation> readPendingByUserIdAndTime(String partition, String code, long userId, long startTime, long endTime, long from, String direct, int size);
	
	NPendingDelegation readOnePending(String partition, String bqCode, long userId, int multiple);
	
	List<NPendingDelegation>readPendingByUserIdAndSymbol(String partition, String bqCode, long userId, String orderIds);
}
