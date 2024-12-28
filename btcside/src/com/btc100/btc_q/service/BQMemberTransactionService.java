package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.BQMemberTransaction;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.vo.BQTransVo;
import com.btc100.btc_q.vo.PagerModel;

public interface BQMemberTransactionService {

	public List<BQMemberTransaction> getBQMemberTrasaction(long fUserId, String transactionCode, int directionSell);
	
	public BQMemberTransaction getMemberTransaction(String code, long fUserId,int trade_type_id,int mutiple);
	
	/**
	 * 通过用户ID获取用户的持仓
	 * 
	 * @param UsersId
	 * @return
	 */
	public List<BQMemberTransaction> getBQMemberTrasactionByUsers(String UsersId);
	
	public List<BQMemberTransaction> getTransactionByUserId(String bqCode, long userId);

	public List<BQMemberTransaction> findTransByCode(String bqCode);

	public double getHoldCount(String tranCode);

	public boolean lockToTrade(long id);
	
	public void unlockToTrade(long id);
	
	public boolean lockToAdjustSpare(long id);
	
	public void unlockToAdjustSpare(long id);
	
	public BQMemberTransaction get(Long id);
	
	public void changeFlagForBC(long transactionId, BigDecimal indexPrice, BigDecimal coefficient);
	
	public List<BQMemberTransaction> findBCTransactions(long transactionId);
	
	public long getUnix_TIMESTAMP();
	
	int updateMemberTransByID(long mtid, BigDecimal thisHoldCount,BigDecimal thisFreeze,BigDecimal thisPrice, BigDecimal thisDeposit, BigDecimal thisFromremain,
			BigDecimal thisFrozenremain, BigDecimal thisFrozenCount, BigDecimal thisMinFrozenremain,BigDecimal newcommission);
	
	public BQMemberTransaction getMemberTransactionById(long id);
	
	public List<BQMemberTransaction> findMaxProfitRateTransactions(long transactionId, int tradeType, BigDecimal price);
	
	public List<BQMemberTransaction> findLossOrRemain(long transactionId, BigDecimal price, BigDecimal coefficient);
	
	public List<BQMemberTransaction> findHaveFundingFee(long transactionId);
	
	public void delete(BQMemberTransaction trans);
	
	public void adjustRemain(long mId, long userId, String code, BigDecimal changeRemain, BigDecimal changeFrozenRemain) throws ServiceException;
	
	public void adjustFundingFee(long mId, long userId, String code, BigDecimal changeAmount) throws ServiceException;
	
	public BigDecimal findSumMinDepositByUser(String code, long userId, long except);
	
	public BigDecimal findSumHoldsByUser(String code, long userId, long except);
	
	public int deleteZEROTran(BQMemberTransaction trans);
	
	public List<Object[]> findSumHold(int type);
	
	public BigDecimal getSumEliteHold(String code, int type, int eliteCount);
	
	public int updateAveragePrice(String code, BigDecimal addPrice);
	
	
	/************************************ for read only **************************************/
	
	public List<BQMemberTransaction> readBQMemberTrasaction(long fUserId, String transactionCode, int directionSell);
	
	public List<BQMemberTransaction> readBQMemberTrasactionByUserId(long fUserId);
	
	
	public BQMemberTransaction readMemberTransaction(String code, long fUserId,int trade_type_id,int mutiple);
	
	public List<BQMemberTransaction> readTransactionByUserId(String bqCode, long userId);
	
	public BigDecimal readDepositByUid(Long uid);
	
	/**
	 * 用户持仓风险度
	 * @param lastPrice 最新成交价
	 * @param bqCode 合约号
	 * @param coefficient 杠杆系数
	 * @param type 方向 1买2卖
	 * @return
	 */
	public List<Object[]> readDegreerisk(BigDecimal lastPrice, String bqCode, BigDecimal coefficient, int type);
	
	public List<Object[]> readHolds(String bqCode, int type, BigDecimal coefficient, String orderBy);
	
	public BQMemberTransaction readById(Long id);
	
	/**
	 * 找出所有用户的数量
	 * @param bqCode 合约号
	 * @param state 状态 1持仓，其他冻结
	 * @param type 类型(1:多单开仓,买多/2:空头开仓,卖空)
	 * @return
	 */
	public BigDecimal readAllUserAmount(String bqCode, int state, int type);
	
	public BQTransVo readHandleData(BQMemberTransaction memberTransaction, int dir, BigDecimal lastPrice, BigDecimal vip, int stopGL, 
			int digits, BigDecimal buy1Price, BigDecimal sell1Price, BigDecimal coefficient);
	
	public List<Object[]> readSumHoldsOrderByUser(long userId);
	
	List<Object[]> readDepositAndFloatingPL(long userId);
	
	BQMemberTransaction readOneMemberTransaction(String code, long userId, BigDecimal multiple);
	
	List<BQMemberTransaction> readMemberTransaction(String code, long userId, BigDecimal multiple);
	
	PagerModel readAllMemberTransacton(String code);
}
