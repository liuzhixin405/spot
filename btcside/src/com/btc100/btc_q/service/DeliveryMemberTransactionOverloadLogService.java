package com.btc100.btc_q.service;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.DeliveryMemberTransactionOverloadLog;
import com.btc100.btc_q.vo.PagerModel;

public interface DeliveryMemberTransactionOverloadLogService { 
	
	public void savLog(DeliveryMemberTransactionOverloadLog log);
	
	
	/************************************ for read only **************************************/
	
	public PagerModel readAll(long startTime, long endTime, Long uid, String code);
	
	public BigDecimal readSumBalance(long startTime, long endTime, Long uid, String code);
}
