package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.DeliveryContractAccountLog;
import com.btc100.btc_q.vo.PagerModel;

public interface DeliveryContractAccountLogService {

	public void insertLog(DeliveryContractAccountLog log);
	
	void createAccountLogBak(String code);
	
	void insertAccountLogBak(String code, long endTime);
	
	
	/************************************ for read only **************************************/
	
	/**
	 * 分页展示
	 * @param userId 用户ID
	 * @param bqCode 合约号
	 * @param operation 操作类型(0:其它/1:转入/2:转出/3:交易/4:追加保证金)
	 * @return
	 */
	public PagerModel readPageAccount(long userId, String bqCode, int operation, long startTime, long endTime);
	
	public List<DeliveryContractAccountLog> readByUserIdAndTime(String bqCode, int operation, long userId, long from, String direct, long startTime, long endTime, int limit);
	
	public DeliveryContractAccountLog readByLogIdAndUserId(String bqCode, long logId, long userId);
	
	public DeliveryContractAccountLog readByTransNoAndUserId(String bqCode, String transNo, long userId);
	
}
