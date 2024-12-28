package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.VArbitrageSetting;

public interface VArbitrageSettingService {

	VArbitrageSetting getArbitrageSettingByCode(String code);

	void save(VArbitrageSetting setting);
	
	void update(VArbitrageSetting setting);
	
	
	
	///************************************ for read only **************************************/
	
	VArbitrageSetting readArbitrageSettingByCode(String code);
}

