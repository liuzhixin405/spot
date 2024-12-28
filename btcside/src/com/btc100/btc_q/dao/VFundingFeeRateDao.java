package com.btc100.btc_q.dao;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.VFundingFeeRate;
import com.btc100.btc_q.vo.PagerModel;

public interface VFundingFeeRateDao<T> extends BaseDaoI<T> {
	
	VFundingFeeRate getByTime(String symbol, long time);
	
	int updateByTime(String symbol, BigDecimal feeRate, long time);
	
	int clampByTime(String symbol, BigDecimal feeRate, long time);

	VFundingFeeRate saveFeeRate(VFundingFeeRate feeRate);
	
	PagerModel findBySymbol(String symbol);
	
	VFundingFeeRate getLast(String symbol);
}
