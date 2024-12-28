package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQMineSetting;

public interface BQMineSettingService {

	void save(BQMineSetting setting);
	
	void update(BQMineSetting setting);
	
	BQMineSetting get();
	
	
	/************************************ for read only **************************************/
	
	BQMineSetting read();
	
}

