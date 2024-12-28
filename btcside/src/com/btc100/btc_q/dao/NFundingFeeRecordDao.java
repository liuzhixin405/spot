package com.btc100.btc_q.dao;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.NFundingFeeRecord;
import com.btc100.btc_q.vo.PagerModel;

public interface NFundingFeeRecordDao<T> extends BaseDaoI<T> {

	public NFundingFeeRecord insertRecord(String partition, NFundingFeeRecord record);
	
	public BigDecimal sumFundingFee(String partition, String symbol, long userId);
	
	PagerModel findBySymbol(String partition, String symbol, long uid);
	
}
