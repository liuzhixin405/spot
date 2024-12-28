package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.NAccountContract;
import com.btc100.btc_q.vo.PagerModel;

public interface NAccountContractService {
	
	public int UpdateAmount(String partition, long userID, String code, BigDecimal initBtc, BigDecimal btc, BigDecimal lockBtc, BigDecimal settle);
	
	int UpdateMoney(String partition, long userID, String code, BigDecimal btc, BigDecimal lockBtc);
	
	/**
	 * 通过用户ID和合约号查询用户的钱包
	 * @param userID 用户ID
	 * @param code 合约号
	 * @return
	 */
	public NAccountContract findByUidCode(String partition, long userID, String code);
	
	public void createByUidCode(String partition, long userID, String code);
	
	public NAccountContract findBTCByUidCode(String partition, long userID, String code);

	public List<NAccountContract> findGreaterThan100(String partition, String code);
	
	public BigDecimal findSumBalanceByUser(String partition, String code, long userId, long except);
	
	public NAccountContract getOverBalance(String partition, long userID, String code, BigDecimal amount);
	
	
	/************************************ for read only **************************************/
	
	public List<NAccountContract> readAllByUid(String partition, long userID);
	
	public BigDecimal readLockAmountByUid(String partition, long userID);

	public BigDecimal readAmountByUid(String partition, long userID);
	
	public NAccountContract readBTCByUidCode(String partition, long userID, String code);
	
	public PagerModel readCountUserIds(String partition);
	
	public BigDecimal readSumBalanceByUser(String partition, String code, long userId, long except);
	
	public List<Object[]> readBalanceAndFrozen(String partition, long userId);
	
	public NAccountContract readByUidCode(String partition, long userID, String code);
	
	public NAccountContract readWithNullByUidCode(String partition, long userID, String code);

	public PagerModel readRecords(String code, String partition);

}
