package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.NStockSetting;
import com.btc100.btc_q.vo.PagerModel;

public interface NStockSettingService {
	
	public void add(String partition, NStockSetting o);
	
	public void update(String partition, NStockSetting o);
	
	public void deleteById(String partition, int id);
	
	public NStockSetting get(String partition);
	
	public NStockSetting getByTransId(String partition, long transactionId);
	

	/************************************ for read only **************************************/
	
	public PagerModel readAll(String partition);
	
	public NStockSetting read(String partition);
	
	public NStockSetting readById(String partition, int id);
	
	public NStockSetting readByTransId(String partition, long transactionId);
}
