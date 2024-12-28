package com.btc100.btc_q.dao;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.NTransactionFeeRate;

public interface NTransactionFeeRateDao<T> extends BaseDaoI<T> {
	
	NTransactionFeeRate getByCodeAndLevel(String partition, String code, long levelId);
	
	public NTransactionFeeRate findLevelByEatRate(String partition, String code, BigDecimal rate);
	
	NTransactionFeeRate getByCodeAndLevelNoCache(String partition, String code, long levelId);
	
	public int insert(String partition, NTransactionFeeRate form);
	
	public int update(String partition, NTransactionFeeRate form);
	
	public int delete(String partition, NTransactionFeeRate form);
}
