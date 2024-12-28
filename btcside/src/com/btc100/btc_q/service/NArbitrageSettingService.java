package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.NArbitrageSetting;

public interface NArbitrageSettingService {

	NArbitrageSetting getArbitrageSettingByCode(String partition, String code);

	void save(String partition, NArbitrageSetting setting);
	
	void update(String partition, NArbitrageSetting setting);
	
	
	
	///************************************ for read only **************************************/
	
	NArbitrageSetting readArbitrageSettingByCode(String partition, String code);
}

