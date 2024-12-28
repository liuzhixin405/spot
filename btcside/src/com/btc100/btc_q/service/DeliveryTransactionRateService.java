package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.DeliveryTransactionRate;

public interface DeliveryTransactionRateService {

	public DeliveryTransactionRate getByCode(String transactionCode);

	public void saveBqTransRate(DeliveryTransactionRate bqTransRate);

	public void updateBqTransRate(DeliveryTransactionRate bqTransRate);

	public void delBqTransRate(long parseInt);
	
	
	/************************************ for read only **************************************/
	
	public List<DeliveryTransactionRate> readList();
	
	public DeliveryTransactionRate readByCode(String transactionCode);
	
	public List<DeliveryTransactionRate> readAll();
	
	public DeliveryTransactionRate readTransRateById(long id);

}
