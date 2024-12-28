package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.VTransactionRate;

public interface VTransactionRateService {

	VTransactionRate getByCode(String transactionCode);

	public void saveBqTransRate(VTransactionRate vTransRate);

	public void updateBqTransRate(VTransactionRate vTransRate);

	public void delBqTransRate(long parseInt);

	
	///************************************ for read only **************************************/
	
	List<VTransactionRate> readList();
	
	VTransactionRate readByCode(String transactionCode);
}
