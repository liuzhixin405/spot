package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.DeliveryArbitrageSetting;

public interface DeliveryArbitrageSettingService {

	DeliveryArbitrageSetting getArbitrageSettingByCode(String code);

	void save(DeliveryArbitrageSetting setting);
	
	void update(DeliveryArbitrageSetting setting);
	
	
	/************************************ for read only **************************************/
	
	DeliveryArbitrageSetting readArbitrageSettingByCode(String code);
}

