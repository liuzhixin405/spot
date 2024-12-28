package com.btc100.btc_q.dao;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.DeliveryTransactionFeeRate;

public interface DeliveryTransactionFeeRateDao<T> extends BaseDaoI<T> {
	
	DeliveryTransactionFeeRate getByCodeAndLevel(String code, long levelId);
	
	public DeliveryTransactionFeeRate findLevelByEatRate(String code, BigDecimal rate);
	
	DeliveryTransactionFeeRate getByCodeAndLevelNoCache(String code, long levelId);
}
