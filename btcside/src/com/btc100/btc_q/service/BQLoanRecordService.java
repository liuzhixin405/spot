package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.BQLoanRecord;
import com.btc100.btc_q.entity.BQLoanSetting;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.vo.PagerModel;

public interface BQLoanRecordService {

	void saveRecord(BQLoanRecord record, BQLoanSetting setting) throws ServiceException;
	
	void backCoin(BQLoanRecord record, BigDecimal amount) throws ServiceException;
	
	void closeCoin(BQLoanRecord record, BigDecimal lastPrice) throws ServiceException;
	
	void adjustPledge(BQLoanRecord record, BigDecimal amount, BigDecimal closeRate, BigDecimal price) throws ServiceException;
	
	List<BQLoanRecord> findAllLargeRate(BigDecimal price, int pledge, int borrow, BigDecimal rate);
	
	List<BQLoanRecord> findAllOverTime();
	
	List<BQLoanRecord> findAllOverCloseTime(long addTime);
	
	
	
	/************************************ for read only **************************************/
	
	
	PagerModel readAllByUserId(long userId, int borrowId, int state);
	
	PagerModel readAll(long borrowId, int state);
	
	BQLoanRecord readById(long id);
	
	BigDecimal readSumBorrow(long userId, int borrowId, int state);
	
	BigDecimal readSumPledge(long userId, int ledgeId, int state);
	
	BigDecimal readSumBorrowOut(long borrowUid, int borrowId, int state);
	
	BigDecimal readSumBorrowIncome(long borrowUid, int borrowId, int state);
}
