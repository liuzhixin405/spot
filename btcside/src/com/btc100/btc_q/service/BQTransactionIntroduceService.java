package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQTransactionIntroduce;
import com.btc100.btc_q.vo.PagerModel;

public interface BQTransactionIntroduceService {

	void add(BQTransactionIntroduce intrduce);

	void delete(BQTransactionIntroduce intrduce);

	void update(BQTransactionIntroduce intrduce);
	
	BQTransactionIntroduce getById(long id);

	PagerModel findAllpage(int type);

	
	/************************************ for read only **************************************/
	
	BQTransactionIntroduce readByCode(String code, int type);
	
	BQTransactionIntroduce readByCode(String partition, String code);
	
	PagerModel readAllpage(String partition, int type);
}
