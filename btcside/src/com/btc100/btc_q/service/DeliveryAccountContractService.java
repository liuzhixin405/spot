package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.DeliveryAccountContract;
import com.btc100.btc_q.vo.PagerModel;

public interface DeliveryAccountContractService {
	
	public int UpdateAmount(long userID, String code, BigDecimal initBtc, BigDecimal btc, BigDecimal lockBtc, BigDecimal settle);
	
	int UpdateMoney(long userID, String code, BigDecimal btc, BigDecimal lockBtc);
	
	/**
	 * 通过用户ID和合约号查询用户的钱包
	 * @param userID 用户ID
	 * @param code 合约号
	 * @return
	 */
	public DeliveryAccountContract findByUidCode(long userID, String code);
	
	public void createByUidCode(long userID, String code);
	
	public DeliveryAccountContract findBTCByUidCode(long userID, String code);

	public List<DeliveryAccountContract> findGreaterThan100(String code);
	
	public List<DeliveryAccountContract> findGreaterThanZero(String code, int size);
	
	public BigDecimal findSumBalanceByUser(String code, long userId, long except);
	
	
	/************************************ for read only **************************************/
	
	public List<DeliveryAccountContract> readAllByUid(long userID);
	
	public BigDecimal readLockAmountByUid(long userID);

	public BigDecimal readAmountByUid(long userID);
	
	public DeliveryAccountContract readBTCByUidCode(long userID, String code);
	
	public PagerModel readCountUserIds();
	
	public BigDecimal readSumBalanceByUser(String code, long userId, long except);
	
	public List<Object[]> readBalanceAndFrozen(long userId);
	
	public DeliveryAccountContract readByUidCode(long userID, String code);
	
	public DeliveryAccountContract readWithNullByUidCode(long userID, String code);
}
