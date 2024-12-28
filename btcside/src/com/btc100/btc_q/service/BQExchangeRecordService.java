package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.BQExchangeRecord;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.vo.PagerModel;

public interface BQExchangeRecordService {

	void saveRecord(BQExchangeRecord record) throws ServiceException;
	
	void adminExchange(BQExchangeRecord record) throws ServiceException;
	
	void update(BQExchangeRecord setting);
	
	List<BQExchangeRecord> findByState(int state, int limit);
	
	List<BQExchangeRecord> findSubmitAndChecked(int limit, int currencyId);
	
	void changeState(long recordId, int state, int oldState, String note);
	
	void processRecord(BQExchangeRecord record) throws ServiceException;
	
	void confirmRecord(BQExchangeRecord record, BigDecimal oriDealNums, BigDecimal obtainNums, BigDecimal dealCash, String note) throws ServiceException;
	
	void cancelRecord(BQExchangeRecord record, String note) throws ServiceException;
	
	void manualRecord(BQExchangeRecord record, String note) throws ServiceException;
	
	void addHedgingTimesAndNote(long recordId, String note);
	
	void orderSuccess(long recordId, String orderId, String note);
	
	BigDecimal sumFrozen(long uid, int currencyId);
	
	BigDecimal sumDeduction(long uid, int currencyId);
	
	BigDecimal sumObtain(long uid, int currencyId);
	
	
	/************************************ for read only **************************************/
	
	BQExchangeRecord readById(long id);
	
	PagerModel readRecords(long uid, int currencyId, int ori, int target);
	
	PagerModel readRecordsOrderByTime(long uid, int currencyId, int ori, int target);
	
	List<BQExchangeRecord> readByUidAndTime(long uid, long startTime, long endTime, long from, String direct, int size, int ori, int target);
	
	BQExchangeRecord readByIdAndURecordId(long uid, long recordId);
}

