package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.BQHedgingRecord;
import com.btc100.btc_q.vo.PagerModel;

public interface BQHedgingRecordService {

	BQHedgingRecord getRecordById(long id);

	void save(BQHedgingRecord setting);
	
	void updateState(long id, int state, BigDecimal price, String note);
	
	void updateSendTimesAndNote(long id, BigDecimal price, String note);
	
	List<BQHedgingRecord> findRecordsByCodeAndState(String code, int state);
	
	List<BQHedgingRecord> findRecordsByCodeAndStateBetweenTime(String code, int state, long beginTime, long endTime);
	
	void cancelRecord(BQHedgingRecord record, String note);
	
	
	/************************************ for read only **************************************/
	
	PagerModel readPagerModel(String transactionCode);
	
	BQHedgingRecord readCountFailedByTime(String code, long beginTime, long endTime, int direction);
}

