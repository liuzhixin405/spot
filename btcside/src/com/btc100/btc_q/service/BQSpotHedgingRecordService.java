package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.BQSpotHedgingRecord;
import com.btc100.btc_q.vo.PagerModel;

public interface BQSpotHedgingRecordService {

	BQSpotHedgingRecord getRecordById(long id);

	void save(BQSpotHedgingRecord setting);
	
	void updateState(long id, int state, BigDecimal price, String note);
	
	void updateSendTimesAndNote(long id, BigDecimal price, String note);
	
	List<BQSpotHedgingRecord> findRecordsByCodeAndState(String code, int state);
	
	List<BQSpotHedgingRecord> findRecordsByCodeAndStateBetweenTime(String code, int state, long beginTime, long endTime);
	
	void cancelRecord(BQSpotHedgingRecord record, String note);
	
	
	/************************************ for read only **************************************/
	
	BQSpotHedgingRecord readCountFailedByTime(String code, long beginTime, long endTime, int direction);
	
	PagerModel readModel(String transactionCode);
}

