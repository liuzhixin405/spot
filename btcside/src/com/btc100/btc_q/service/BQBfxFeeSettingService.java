package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQBfxFeeSetting;

public interface BQBfxFeeSettingService {

	void save(BQBfxFeeSetting setting);
	
	void update(BQBfxFeeSetting setting);
	
	BQBfxFeeSetting get();
	
	/************************************ for read only **************************************/
	
	BQBfxFeeSetting readOne();
}

