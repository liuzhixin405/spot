package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQSpotQuantSetting;

public interface BQSpotQuantSettingService {
	
	public void update(BQSpotQuantSetting o);
	
	public BQSpotQuantSetting get();
	
	
	/************************************ for read only **************************************/
	
	public BQSpotQuantSetting read();
}
