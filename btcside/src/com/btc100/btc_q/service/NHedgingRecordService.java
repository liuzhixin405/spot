package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.NHedgingRecord;
import com.btc100.btc_q.vo.PagerModel;

public interface NHedgingRecordService {

	NHedgingRecord getRecordById(String partition, long id);

	void save(String partition, NHedgingRecord setting);
	
	void updateState(String partition, long id, int state, BigDecimal price, String note);
	
	void updateSendTimesAndNote(String partition, long id, BigDecimal price, String note);
	
	List<NHedgingRecord> findRecordsByCodeAndState(String partition, String code, int state);
	
	List<NHedgingRecord> findRecordsByCodeAndStateBetweenTime(String partition, String code, int state, long beginTime, long endTime);
	
	void cancelRecord(String partition, NHedgingRecord record, String note);
	
	
	/************************************ for read only **************************************/
	
	PagerModel readPagerModel(String partition, String transactionCode);
	
	NHedgingRecord readCountFailedByTime(String partition, String code, long beginTime, long endTime, int direction);
}

