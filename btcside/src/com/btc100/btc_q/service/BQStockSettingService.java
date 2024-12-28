package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQStockSetting;
import com.btc100.btc_q.vo.PagerModel;

public interface BQStockSettingService {
	
	public void add(BQStockSetting o);
	
	public void update(BQStockSetting o);
	
	public void deleteById(int id);
	
	public BQStockSetting get();
	
	public BQStockSetting getByTransId(long transactionId);
	

	/************************************ for read only **************************************/
	
	public PagerModel readAll();
	
	public BQStockSetting read();
	
	public BQStockSetting readById(int id);
	
	public BQStockSetting readByTransId(long transactionId);
}
