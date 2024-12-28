package com.btc100.btc_q.service;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.NTradeLog;
import com.btc100.btc_q.entity.SpotTradeLog;
import com.btc100.btc_q.entity.TradeLog;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.vo.PagerModel;

public interface BQMineRecordService {

	void mine(TradeLog trade, int currencyId, BigDecimal amount, BigDecimal mineRate, long settingId) throws ServiceException;
	
	void mine(NTradeLog trade, int currencyId, BigDecimal amount, BigDecimal mineRate, long settingId) throws ServiceException;

	void mine(SpotTradeLog trade, int currencyId, BigDecimal amount, BigDecimal mineRate,
			  long settingId, BigDecimal personMax, BigDecimal fee) throws ServiceException;
	
	
	/************************************ for read only **************************************/
	
	PagerModel readRecords(long uid, long startTime, long endTime);

	PagerModel readRecords(long uid, int tradeMode, long startTime, long endTime);
	
	BigDecimal readSumFeeBetweenTime(long uid, long startTime, long endTime);
}
