package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.NTransactionRate;

public interface NTransactionRateService {

	public NTransactionRate getByCode(String partition, String transactionCode);

	public void saveBqTransRate(String partition, NTransactionRate bqTransRate);

	public void updateBqTransRate(String partition, NTransactionRate bqTransRate);

	public void delBqTransRate(String partition, long parseInt);
	
	
	/************************************ for read only **************************************/
	
	public List<NTransactionRate> readList(String partition);
	
	public NTransactionRate readByCode(String partition, String transactionCode);
	
	public List<NTransactionRate> readAll(String partition);
	
	public NTransactionRate readTransRateById(String partition, long id);

}
