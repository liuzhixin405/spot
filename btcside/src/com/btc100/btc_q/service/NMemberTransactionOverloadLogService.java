package com.btc100.btc_q.service;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.NMemberTransactionOverloadLog;
import com.btc100.btc_q.vo.PagerModel;

public interface NMemberTransactionOverloadLogService { 
	
	public void savLog(String partition, NMemberTransactionOverloadLog log);
	
	/************************************ for read only **************************************/
	
	public PagerModel readAll(String partition, long startTime, long endTime, Long uid, String code);
	
	public BigDecimal readSumBalance(String partition, long startTime, long endTime, Long uid, String code);
}
