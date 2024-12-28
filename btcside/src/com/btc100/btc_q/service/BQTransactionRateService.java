package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQTransactionRate;

public interface BQTransactionRateService {

	public BQTransactionRate getByCode(String transactionCode);

	public void saveBqTransRate(BQTransactionRate bqTransRate);

	public void updateBqTransRate(BQTransactionRate bqTransRate);

	public void delBqTransRate(long parseInt);
	
	
	/************************************ for read only **************************************/
	
	public List<BQTransactionRate> readList();
	
	public BQTransactionRate readByCode(String transactionCode);
	
	public List<BQTransactionRate> readAll();
	
	public BQTransactionRate readTransRateById(long id);

}
