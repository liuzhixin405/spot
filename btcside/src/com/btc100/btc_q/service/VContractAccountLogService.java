package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.VContractAccountLog;
import com.btc100.btc_q.vo.PagerModel;

public interface VContractAccountLogService {
	
	public void insertLog(VContractAccountLog log);
	
	void createAccountLogBak(String code);
	
	void insertAccountLogBak(String code, long endTime);
	
	
	
	///************************************ for read only **************************************/
	
	/**
	 * 分页展示
	 * @param userId 用户ID
	 * @param vCode 合约号
	 * @param operation 操作类型(0:其它/1:转入/2:转出/3:交易/4:追加保证金)
	 * @return
	 */
	public PagerModel readPageAccount(long userId, String vCode, int operation, long startTime, long endTime);
	
	List<VContractAccountLog> readByUserIdAndTime(String vCode, int operation, long userId, long from, String direct, long startTime, long endTime, int limit);
	
	VContractAccountLog readByLogIdAndUserId(String vCode, long logId, long userId);
	
	VContractAccountLog readByTransNoAndUserId(String vCode, String transNo, long userId);
}
