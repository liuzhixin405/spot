package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQTransactionFeeRate;
import com.btc100.btc_q.vo.PagerModel;

public interface BQTransactionFeeRateService {

	void save(BQTransactionFeeRate rate);
	
	void update(BQTransactionFeeRate rate);
	
	void delete(BQTransactionFeeRate rate);
	
	BQTransactionFeeRate getByCodeAndLevel(String code, long levelId);
	
	/************************************ for read only **************************************/
	
	BQTransactionFeeRate readById(long id);
	
	BQTransactionFeeRate readByCodeAndLevel(String code, long levelId);
	
	BQTransactionFeeRate readByCodeAndLevelNoCache(String code, long levelId);
	
	PagerModel readByCode(String code);
}

