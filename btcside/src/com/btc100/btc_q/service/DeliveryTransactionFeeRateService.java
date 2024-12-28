package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.DeliveryTransactionFeeRate;
import com.btc100.btc_q.vo.PagerModel;

public interface DeliveryTransactionFeeRateService {

	void save(DeliveryTransactionFeeRate rate);
	
	void update(DeliveryTransactionFeeRate rate);
	
	void delete(DeliveryTransactionFeeRate rate);
	
	DeliveryTransactionFeeRate getByCodeAndLevel(String code, long levelId);
	
	/************************************ for read only **************************************/
	
	DeliveryTransactionFeeRate readById(long id);
	
	DeliveryTransactionFeeRate readByCodeAndLevel(String code, long levelId);
	
	DeliveryTransactionFeeRate readByCodeAndLevelNoCache(String code, long levelId);
	
	PagerModel readByCode(String code);
}

