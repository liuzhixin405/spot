package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.VMemberTransaction;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.vo.BQTransVo;

public interface VMemberTransactionService {

	public List<VMemberTransaction> getVMemberTrasaction(long fUserId, String transactionCode, int directionSell);
	
	public VMemberTransaction getMemberTransaction(String code, long fUserId,int trade_type_id,int mutiple);
	
	public List<VMemberTransaction> findTransByCode(String bqCode);

	public double getHoldCount(String tranCode);

	public boolean lockToTrade(long id);
	
	public void unlockToTrade(long id);
	
	public boolean lockToAdjustSpare(long id);
	
	public void unlockToAdjustSpare(long id);
	
	public List<Object[]> findHolds(String vCode, int type, BigDecimal coefficient);
	
	public VMemberTransaction get(Long id);

	/**
	 * 找出所有用户的数量
	 * @param vCode 合约号
	 * @param state 状态 1持仓，其他冻结
	 * @param type 类型(1:多单开仓,买多/2:空头开仓,卖空)
	 * @return
	 */
	public BigDecimal findAllUserAmount(String vCode, int state, int type);
	
	public void changeFlagForBC(long transactionId, BigDecimal indexPrice);
	
	public List<VMemberTransaction> findBCTransactions(long transactionId);
	
	public long getUnix_TIMESTAMP();
	
	int updateMemberTransByID(long mtid, BigDecimal thisHoldCount,BigDecimal thisFreeze,BigDecimal thisPrice, BigDecimal thisDeposit, BigDecimal thisFromremain,
			BigDecimal thisFrozenremain, BigDecimal thisFrozenCount, BigDecimal thisMinFrozenremain,BigDecimal newcommission);
	
	
	public VMemberTransaction getMemberTransactionById(long id);
	
	public List<VMemberTransaction> findMaxProfitRateTransactions(long transactionId, int tradeType, BigDecimal price);
	
	public List<VMemberTransaction> findLossOrRemain(long transactionId, BigDecimal price);
	
	public List<VMemberTransaction> findHaveFundingFee(long transactionId);
	
	public List<Long> findLossUser(long transactionId, BigDecimal price);
	
	public void delete(VMemberTransaction trans);
	
	public void adjustRemain(long mId, long userId, String code, BigDecimal changeRemain, BigDecimal changeFrozenRemain) throws ServiceException;
	
	public void adjustFundingFee(long mId, long userId, String code, BigDecimal changeAmount) throws ServiceException;
	
	public BigDecimal findSumMinDepositByUser(String code, long userId, long except);
	
	public BigDecimal findSumHoldsByUser(String code, long userId, long except);
	
	public List<Object[]> findSumIncomeAndDeposit(String code, long except, BigDecimal price);
	
	public int deleteZEROTran(long mId);
	
	
	
	
	///************************************ for read only **************************************/
	
	public List<VMemberTransaction> readVMemberTrasaction(long fUserId, String transactionCode, int directionSell);
	
	public List<VMemberTransaction> readVMemberTrasactionByUserId(long fUserId);
	
	public VMemberTransaction readMemberTransaction(String code, long fUserId,int trade_type_id,int mutiple);
	
	public List<VMemberTransaction> readTransactionByUserId(String vCode, long userId);
	
	public BigDecimal readDepositByUid(Long id);
	
	public BQTransVo readData(VMemberTransaction memberTransaction, int dir, BigDecimal lastPrice, BigDecimal vip, int stopGL, 
			int digits, BigDecimal buy1Price, BigDecimal sell1Price);
	
	VMemberTransaction readOneMemberTransaction(String code, long userId, BigDecimal multiple);
	
	List<VMemberTransaction> readMemberTransaction(String code, long userId, BigDecimal multiple);
}
