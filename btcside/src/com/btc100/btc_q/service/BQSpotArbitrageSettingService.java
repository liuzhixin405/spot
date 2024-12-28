package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQSpotArbitrageSetting;

public interface BQSpotArbitrageSettingService {

	BQSpotArbitrageSetting getArbitrageSettingByCode(String code);

	void save(BQSpotArbitrageSetting setting);
	
	void update(BQSpotArbitrageSetting setting);
	
	
	/************************************ for read only **************************************/
	
	BQSpotArbitrageSetting readArbitrageSettingByCode(String code);
}

