package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQBonusSetting;

public interface BQBonusSettingService {

	void save(BQBonusSetting setting);
	
	void update(BQBonusSetting setting);
	
	void firstDone(long id);
	
	BQBonusSetting get();
	
	
	/************************************ for read only **************************************/
	
	BQBonusSetting readOne();
}

