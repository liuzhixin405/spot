package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQAccountAmountLog;

import java.util.List;

import com.btc100.btc_q.vo.PagerModel;

public interface BQAccountAmountLogService {

	void saveAmountlog(BQAccountAmountLog amountLog);

	void save(BQAccountAmountLog bqal);

	void createAccountAmountLogBak();

	void insertAccountAmountLogBak(long endTime);
	
	/************************************ for read only **************************************/
	
	PagerModel readListRechargeWithdraw(long id);
	
	PagerModel readOTCRecords(long uid, int currencyId, int operation);
	
	List<BQAccountAmountLog> readByUserAndTime(long uid, long from, String direct, long startTime, long endTime, int limit);
}
