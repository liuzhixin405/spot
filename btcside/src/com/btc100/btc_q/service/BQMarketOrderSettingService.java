package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQMarketOrderSetting;

public interface BQMarketOrderSettingService {

	void save(BQMarketOrderSetting setting);
	
	void update(BQMarketOrderSetting setting);
	
	BQMarketOrderSetting get();
	
	
	/************************************ for read only **************************************/
	
	BQMarketOrderSetting read();
	
}

