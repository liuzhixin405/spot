package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.VAccountContract;

public interface VAccountContractService {

	public int UpdateAmount(long userID, String code, BigDecimal initBtc, BigDecimal btc, BigDecimal lockBtc, BigDecimal settle);
	
	int UpdateMoney(long userID, String code, BigDecimal btc, BigDecimal lockBtc);
	/**
	 * 通过用户ID和合约号查询用户的钱包（如果不存在此用户的钱包，则添加一条数据）
	 * @param userID 用户ID
	 * @param code 合约号
	 * @return
	 */
	public VAccountContract findByUidCode(long userID, String code);
	
	public void createByUidCode(long userID, String code);
	
	public VAccountContract findBTCByUidCode(long userID, String code);
	
	public VAccountContract getOverBalance(long userID, String code, BigDecimal amount);
	
	int deletetobak(String code);

	public BigDecimal findSumBalanceByUser(String code, long userId, long except);
	
	
	
	///************************************ for read only **************************************/
	
	public VAccountContract readByUidCode(long userID, String code);
	
	public List<VAccountContract> readAllByUid(long userID);
	
	/**
	 * 通过用户ID查询用户的挂单冻结
	 * @param userID 用户ID
	 * @return
	 */
	public BigDecimal readLockAmountByUid(long userID);

	/**
	 * 通过用户ID查询用户的可用数量
	 * @param userID 用户ID
	 * @return
	 */
	public BigDecimal readAmountByUid(long userID);
	
	public VAccountContract readBTCByUidCode(long userID, String code);
	
	public VAccountContract readWithNullByUidCode(long userID, String code);

}
