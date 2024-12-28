package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.NContractAccountLog;
import com.btc100.btc_q.vo.PagerModel;

public interface NContractAccountLogService {
	
	public void insertLog(String partition, NContractAccountLog log);
	
	void createAccountLogBak(String partition, String code);
	
	void insertAccountLogBak(String partition, String code, long endTime);
	
	
	
	///************************************ for read only **************************************/
	
	/**
	 * 分页展示
	 * @param userId 用户ID
	 * @param vCode 合约号
	 * @param operation 操作类型(0:其它/1:转入/2:转出/3:交易/4:追加保证金)
	 * @return
	 */
	public PagerModel readPageAccount(String partition, long userId, String vCode, int operation, long startTime, long endTime);
	
	List<NContractAccountLog> readByUserIdAndTime(String partition, String vCode, int operation, long userId, long from, String direct, long startTime, long endTime, int limit);
	
	NContractAccountLog readByLogIdAndUserId(String partition, String vCode, long logId, long userId);
	
	NContractAccountLog readByTransNoAndUserId(String partition, String vCode, String transNo, long userId);
}
