package com.btc100.btc_q.dao;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.FundingFeeRate;
import com.btc100.btc_q.vo.PagerModel;

public interface FundingFeeRateDao<T> extends BaseDaoI<T> {
	
	FundingFeeRate getByTime(String symbol, long time);
	
	int updateByTime(String symbol, BigDecimal feeRate, long time);
	
	int clampByTime(String symbol, BigDecimal feeRate, long time);

	FundingFeeRate saveFeeRate(FundingFeeRate feeRate);
	
	PagerModel findBySymbol(String symbol);
	
	FundingFeeRate getLast(String symbol);
}
