package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.DeliveryOverloadMatchSetting;

public interface DeliveryOverloadMatchSettingService {

	DeliveryOverloadMatchSetting getSettingByCode(String code);

	void save(DeliveryOverloadMatchSetting setting);
	
	void update(DeliveryOverloadMatchSetting setting);
	
	
	/************************************ for read only **************************************/
	
	DeliveryOverloadMatchSetting readSettingByCode(String code);
}

