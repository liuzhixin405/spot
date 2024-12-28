package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.NMemberTransaction;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.vo.BQTransVo;
import com.btc100.btc_q.vo.PagerModel;

public interface NMemberTransactionService {

	public List<NMemberTransaction> getMemberTrasaction(String partition, long fUserId, String transactionCode, int directionSell);
	
	public NMemberTransaction getMemberTransaction(String partition, String code, long fUserId,int trade_type_id,int mutiple);
	
	/**
	 * 通过用户ID获取用户的持仓
	 * 
	 * @param UsersId
	 * @return
	 */
	public List<NMemberTransaction> getMemberTrasactionByUsers(String partition, String UsersId);
	
	public List<NMemberTransaction> getTransactionByUserId(String partition, String bqCode, long userId);

	public List<NMemberTransaction> findTransByCode(String partition, String bqCode);

	public double getHoldCount(String partition, String tranCode);

	public boolean lockToTrade(String partition, long id);
	
	public void unlockToTrade(String partition, long id);
	
	public boolean lockToAdjustSpare(String partition, long id);
	
	public void unlockToAdjustSpare(String partition, long id);
	
	public NMemberTransaction get(String partition, Long id);
	
	public void changeFlagForBC(String partition, long transactionId, BigDecimal indexPrice, BigDecimal coefficient);
	
	public List<NMemberTransaction> findBCTransactions(String partition, long transactionId);
	
	public long getUnix_TIMESTAMP();
	
	int updateMemberTransByID(String partition, long mtid, BigDecimal thisHoldCount,BigDecimal thisFreeze,BigDecimal thisPrice, BigDecimal thisDeposit, BigDecimal thisFromremain,
			BigDecimal thisFrozenremain, BigDecimal thisFrozenCount, BigDecimal thisMinFrozenremain,BigDecimal newcommission);
	
	public NMemberTransaction getMemberTransactionById(String partition, long id);
	
	public List<NMemberTransaction> findMaxProfitRateTransactions(String partition, long transactionId, int tradeType, BigDecimal price);
	
	public List<NMemberTransaction> findLossOrRemain(String partition, long transactionId, BigDecimal price, BigDecimal coefficient);
	
	public List<NMemberTransaction> findHaveFundingFee(String partition, long transactionId);
	
	public void delete(String partition, NMemberTransaction trans);
	
	public void adjustRemain(String partition, long mId, long userId, String code, BigDecimal changeRemain, BigDecimal changeFrozenRemain) throws ServiceException;
	
	public void adjustFundingFee(String partition, long mId, long userId, String code, BigDecimal changeAmount) throws ServiceException;
	
	public BigDecimal findSumMinDepositByUser(String partition, String code, long userId, long except);
	
	public BigDecimal findSumHoldsByUser(String partition, String code, long userId, long except);
	
	public int deleteZEROTran(String partition, NMemberTransaction trans);
	
	public List<Object[]> findSumHold(String partition, int type);
	
	public BigDecimal getSumEliteHold(String partition, String code, int type, int eliteCount);
	
	public int updateAveragePrice(String partition, String code, BigDecimal addPrice);
	
	
	/************************************ for read only **************************************/
	
	public List<NMemberTransaction> readMemberTrasaction(String partition, long fUserId, String transactionCode, int directionSell);
	
	public List<NMemberTransaction> readMemberTrasactionByUserId(String partition, long fUserId);
	
	
	public NMemberTransaction readMemberTransaction(String partition, String code, long fUserId,int trade_type_id,int mutiple);
	
	public List<NMemberTransaction> readTransactionByUserId(String partition, String bqCode, long userId);
	
	public BigDecimal readDepositByUid(String partition, Long uid);
	
	/**
	 * 用户持仓风险度
	 * @param lastPrice 最新成交价
	 * @param bqCode 合约号
	 * @param coefficient 杠杆系数
	 * @param type 方向 1买2卖
	 * @return
	 */
	public List<Object[]> readDegreerisk(String partition, BigDecimal lastPrice, String bqCode, BigDecimal coefficient, int type);
	
	public List<Object[]> readHolds(String partition, String bqCode, int type, BigDecimal coefficient, String orderBy);
	
	public NMemberTransaction readById(String partition, Long id);
	
	/**
	 * 找出所有用户的数量
	 * @param bqCode 合约号
	 * @param state 状态 1持仓，其他冻结
	 * @param type 类型(1:多单开仓,买多/2:空头开仓,卖空)
	 * @return
	 */
	public BigDecimal readAllUserAmount(String partition, String bqCode, int state, int type);
	
	public BQTransVo readHandleData(String partition, NMemberTransaction memberTransaction, int dir, BigDecimal lastPrice, BigDecimal vip, int stopGL, 
			int digits, BigDecimal buy1Price, BigDecimal sell1Price, BigDecimal coefficient);
	
	public List<Object[]> readSumHoldsOrderByUser(String partition, long userId);
	
	List<Object[]> readDepositAndFloatingPL(String partition, long userId);
	
	NMemberTransaction readOneMemberTransaction(String partition, String code, long userId, BigDecimal multiple);
	
	List<NMemberTransaction> readMemberTransaction(String partition, String code, long userId, BigDecimal multiple);
	
	PagerModel readAllMemberTransacton(String partition, String code);
}
