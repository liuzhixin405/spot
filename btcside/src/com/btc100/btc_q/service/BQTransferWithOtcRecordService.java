package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.BQTransferWithOtcRecord;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.vo.PagerModel;

public interface BQTransferWithOtcRecordService {

	String saveRecord(BQTransferWithOtcRecord record) throws ServiceException;
	
	void update(BQTransferWithOtcRecord record);
	
	List<BQTransferWithOtcRecord> findByState(int state, int limit);
	
	void changeState(long recordId, int state, int oldState, String note);
	
	String confirmRecord(BQTransferWithOtcRecord record, String note) throws ServiceException;
	
	String cancelRecord(BQTransferWithOtcRecord record, String note) throws ServiceException;
	
	BigDecimal sumFrozen(long uid, int currencyId, int direction);
	
	
	/************************************ for read only **************************************/
	
	BQTransferWithOtcRecord readById(long id);
	
	PagerModel readRecords(long uid, int currencyId);
	
	BQTransferWithOtcRecord readByIdAndURecordId(long uid, long recordId);
}

