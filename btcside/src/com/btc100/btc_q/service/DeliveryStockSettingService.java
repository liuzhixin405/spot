package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.DeliveryStockSetting;
import com.btc100.btc_q.vo.PagerModel;

public interface DeliveryStockSettingService {
	
	public void add(DeliveryStockSetting o);
	
	public void update(DeliveryStockSetting o);
	
	public void deleteById(int id);
	
	public DeliveryStockSetting get();
	
	public DeliveryStockSetting getByTransId(long transactionId);
	

	/************************************ for read only **************************************/
	
	public PagerModel readAll();
	
	public DeliveryStockSetting read();
	
	public DeliveryStockSetting readById(int id);
	
	public DeliveryStockSetting readByTransId(long transactionId);
}
