package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.FundingFeeRecord;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.vo.PagerModel;

public interface FundingFeeRecordService {
	
	void doAccountFundingFee(FundingFeeRecord record) throws ServiceException;
	
	void doMemberTransFundingFee(FundingFeeRecord record, long mtId, BigDecimal coff) throws ServiceException;
	
	
	/************************************ for read only **************************************/
	
	PagerModel readBySymbol(String symbol, long uid);
	
	List<FundingFeeRecord> readByUserAndTime(String symbol, long uid, long from, String direct, long startTime, long endTime, int limit);
	
}
