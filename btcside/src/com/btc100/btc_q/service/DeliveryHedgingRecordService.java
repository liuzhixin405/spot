package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.DeliveryHedgingRecord;
import com.btc100.btc_q.vo.PagerModel;

public interface DeliveryHedgingRecordService {

	DeliveryHedgingRecord getRecordById(long id);

	void save(DeliveryHedgingRecord record);
	
	void updateState(long id, int state, BigDecimal price, String note);
	
	void updateSendTimesAndNote(long id, BigDecimal price, String note);
	
	List<DeliveryHedgingRecord> findRecordsByCodeAndState(String code, int state);
	
	List<DeliveryHedgingRecord> findRecordsByCodeAndStateBetweenTime(String code, int state, long beginTime, long endTime);
	
	void cancelRecord(DeliveryHedgingRecord record, String note);
	
	
	/************************************ for read only **************************************/
	
	PagerModel readPagerModel(String transactionCode);
	
	DeliveryHedgingRecord readCountFailedByTime(String code, long beginTime, long endTime, int direction);
}

