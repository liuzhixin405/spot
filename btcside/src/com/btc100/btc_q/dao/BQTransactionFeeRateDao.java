package com.btc100.btc_q.dao;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.BQTransactionFeeRate;

public interface BQTransactionFeeRateDao<T> extends BaseDaoI<T> {
	
	BQTransactionFeeRate getByCodeAndLevel(String code, long levelId);
	
	public BQTransactionFeeRate findLevelByEatRate(String code, BigDecimal rate);
	
	BQTransactionFeeRate getByCodeAndLevelNoCache(String code, long levelId);
}
