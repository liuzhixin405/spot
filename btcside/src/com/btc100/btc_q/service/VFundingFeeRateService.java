package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.VFundingFeeRate;
import com.btc100.btc_q.vo.PagerModel;

public interface VFundingFeeRateService {
	
	VFundingFeeRate getByTime(String symbol, long time);
	
	VFundingFeeRate getLast(String symbol);
	
	int updateByTime(String symbol, BigDecimal feeRate, long time);
	
	int clampByTime(String symbol, BigDecimal feeRate, long time);

	VFundingFeeRate saveFeeRate(VFundingFeeRate feeRate);
	
	void updateRate(VFundingFeeRate feeRate, BigDecimal rate, int count);
	
	void deleteFeeRate(VFundingFeeRate feeRate);

	
	/************************************ for read only **************************************/
	
	VFundingFeeRate readLast(String symbol);
	
	PagerModel readBySymbol(String symbol);

	public List<VFundingFeeRate> readList(String symbol, long from, String direct, long startTime, long endTime, int limit);
}
