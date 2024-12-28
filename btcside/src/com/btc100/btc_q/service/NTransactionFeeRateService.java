package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.NTransactionFeeRate;
import com.btc100.btc_q.vo.PagerModel;

public interface NTransactionFeeRateService {

	void save(String partition, NTransactionFeeRate rate);
	
	void update(String partition, NTransactionFeeRate rate);
	
	void delete(String partition, NTransactionFeeRate rate);
	
	NTransactionFeeRate getByCodeAndLevel(String partition, String code, long levelId);
	
	/************************************ for read only **************************************/
	
	NTransactionFeeRate readById(String partition, long id);
	
	NTransactionFeeRate readByCodeAndLevel(String partition, String code, long levelId);
	
	NTransactionFeeRate readByCodeAndLevelNoCache(String partition, String code, long levelId);
	
	PagerModel readByCode(String partition, String code);
}

