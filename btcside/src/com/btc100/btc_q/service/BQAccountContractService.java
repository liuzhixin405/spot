package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.BQAccountContract;
import com.btc100.btc_q.vo.PagerModel;

public interface BQAccountContractService {
	
	public int UpdateAmount(long userID, String code, BigDecimal initBtc, BigDecimal btc, BigDecimal lockBtc, BigDecimal settle);
	
	int UpdateMoney(long userID, String code, BigDecimal btc, BigDecimal lockBtc);
	
	/**
	 * 通过用户ID和合约号查询用户的钱包
	 * @param userID 用户ID
	 * @param code 合约号
	 * @return
	 */
	public BQAccountContract findByUidCode(long userID, String code);
	
	public void createByUidCode(long userID, String code);
	
	public BQAccountContract findBTCByUidCode(long userID, String code);
	
	public BQAccountContract getOverBalance(long userID, String code, BigDecimal amount);

	public List<BQAccountContract> findGreaterThan100(String code);
	
	public BigDecimal findSumBalanceByUser(String code, long userId, long except);
	
	
	/************************************ for read only **************************************/
	
	public List<BQAccountContract> readAllByUid(long userID);
	
	public BigDecimal readLockAmountByUid(long userID);

	public BigDecimal readAmountByUid(long userID);
	
	public BQAccountContract readBTCByUidCode(long userID, String code);
	
	public PagerModel readCountUserIds();
	
	public BigDecimal readSumBalanceByUser(String code, long userId, long except);
	
	public List<Object[]> readBalanceAndFrozen(long userId);
	
	public BQAccountContract readByUidCode(long userID, String code);
	
	public BQAccountContract readWithNullByUidCode(long userID, String code);
	
	public PagerModel readRecords(String code);
}
