package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.BQBuyBfxBackRecord;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.vo.PagerModel;

public interface BQBuyBfxBackRecordService {

	void saveRecord(BQBuyBfxBackRecord record) throws ServiceException;
	
	void changeState(long recordId, int state, int oldState, String note);
	
	BigDecimal sumAmount(long uid);
	
	BigDecimal sumAmountFrozen(long uid);
	
	BigDecimal sumObtain(long uid, int currencyId);
	
	
	/************************************ for read only **************************************/
	
	PagerModel readRecords(long uid);
	
	List<BQBuyBfxBackRecord> readByUidAndTime(long uid, long startTime, long endTime, long from, String direct, int size);
}

