package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQSpotBetterTokenArbitrageSetting;

public interface BQSpotBetterTokenArbitrageSettingService {

	BQSpotBetterTokenArbitrageSetting getArbitrageSetting();

	void save(BQSpotBetterTokenArbitrageSetting setting);
	
	void update(BQSpotBetterTokenArbitrageSetting setting);
	
	
	/************************************ for read only **************************************/
	
	BQSpotBetterTokenArbitrageSetting readArbitrageSetting();
}

