package com.btc100.btc_q.dao;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.NFundingFeeRate;
import com.btc100.btc_q.vo.PagerModel;

public interface NFundingFeeRateDao<T> extends BaseDaoI<T> {
	
	NFundingFeeRate getByTime(String partition, String symbol, long time);
	
	int updateByTime(String partition, String symbol, BigDecimal feeRate, long time);
	
	int clampByTime(String partition, String symbol, BigDecimal feeRate, long time);

	NFundingFeeRate saveFeeRate(String partition, NFundingFeeRate feeRate);
	
	PagerModel findBySymbol(String partition, String symbol);
	
	NFundingFeeRate getLast(String partition, String symbol);
}
