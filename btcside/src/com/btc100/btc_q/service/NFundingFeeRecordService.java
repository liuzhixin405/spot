package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.NFundingFeeRecord;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.vo.PagerModel;

public interface NFundingFeeRecordService {
	
	void doAccountFundingFee(String partition, NFundingFeeRecord record, int currencyId) throws ServiceException;
	
	void doMemberTransFundingFee(String partition, NFundingFeeRecord record, long mtId, BigDecimal coff, int currencyId) throws ServiceException;
	
	
	/************************************ for read only **************************************/
	
	PagerModel readBySymbol(String partition, String symbol, long uid);
	
	List<NFundingFeeRecord> readByUserAndTime(String partition, String symbol, long uid, long from, String direct, long startTime, long endTime, int limit);
	
}
