package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.NFundingFeeRate;
import com.btc100.btc_q.vo.PagerModel;

public interface NFundingFeeRateService {
	
	NFundingFeeRate getByTime(String partition, String symbol, long time);
	
	NFundingFeeRate getLast(String partition, String symbol);
	
	int updateByTime(String partition, String symbol, BigDecimal feeRate, long time);
	
	int clampByTime(String partition, String symbol, BigDecimal feeRate, long time);

	NFundingFeeRate saveFeeRate(String partition, NFundingFeeRate feeRate);
	
	void updateRate(String partition, NFundingFeeRate feeRate, BigDecimal rate, int count);
	
	void deleteFeeRate(String partition, NFundingFeeRate feeRate);

	
	/************************************ for read only **************************************/
	
	NFundingFeeRate readLast(String partition, String symbol);
	
	PagerModel readBySymbol(String partition, String symbol);

	public List<NFundingFeeRate> readList(String partition, String symbol, long from, String direct, long startTime, long endTime, int limit);
}
