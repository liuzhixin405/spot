package com.btc100.btc_q.dao;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.FundingFeeRecord;
import com.btc100.btc_q.vo.PagerModel;

public interface FundingFeeRecordDao<T> extends BaseDaoI<T> {

	public FundingFeeRecord insertRecord(FundingFeeRecord record);
	
	public BigDecimal sumFundingFee(String symbol, long userId);
	
	PagerModel findBySymbol(String symbol, long uid);
	
}
