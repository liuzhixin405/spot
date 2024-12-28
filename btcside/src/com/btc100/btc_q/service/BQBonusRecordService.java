package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.BQBonusRecord;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.vo.PagerModel;

public interface BQBonusRecordService {

	void bonus(BQBonusRecord record) throws ServiceException;
	
	
	/************************************ for read only **************************************/
	
	PagerModel readRecords(long uid, long startTime, long endTime);
	
	BQBonusRecord readLast(long lastTime, int currencyId);
	
	BigDecimal readSumBonusRate(int currencyId);
	
	List<BQBonusRecord> readByUserAndTime(long uid, long from, String direct, long startTime, long endTime, int limit);	
	
}
