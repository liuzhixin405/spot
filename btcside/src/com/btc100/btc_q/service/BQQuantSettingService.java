package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQQuantSetting;

public interface BQQuantSettingService {
	
	public void update(BQQuantSetting o);
	
	public BQQuantSetting get(String partition);
}
