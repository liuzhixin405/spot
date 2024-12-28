package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQArbitrageSetting;

public interface BQArbitrageSettingService {

	BQArbitrageSetting getArbitrageSettingByCode(String code);

	void save(BQArbitrageSetting setting);
	
	void update(BQArbitrageSetting setting);
	
	
	/************************************ for read only **************************************/
	
	BQArbitrageSetting readArbitrageSettingByCode(String code);
}

