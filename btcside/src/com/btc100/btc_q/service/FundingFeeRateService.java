package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.FundingFeeRate;
import com.btc100.btc_q.vo.PagerModel;

public interface FundingFeeRateService {
	
	FundingFeeRate getByTime(String symbol, long time);
	
	FundingFeeRate getLast(String symbol);
	
	int updateByTime(String symbol, BigDecimal feeRate, long time);
	
	int clampByTime(String symbol, BigDecimal feeRate, long time);

	FundingFeeRate saveFeeRate(FundingFeeRate feeRate);
	
	void updateRate(FundingFeeRate feeRate, BigDecimal rate, int count);
	
	void deleteFeeRate(FundingFeeRate feeRate);

	
	/************************************ for read only **************************************/
	
	FundingFeeRate readLast(String symbol);
	
	PagerModel readBySymbol(String symbol);

	public List<FundingFeeRate> readList(String symbol, long from, String direct, long startTime, long endTime, int limit);
}
