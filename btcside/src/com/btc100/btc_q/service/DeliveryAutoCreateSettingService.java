package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.DeliveryAutoCreateSetting;

public interface DeliveryAutoCreateSettingService {

	DeliveryAutoCreateSetting getByType(String type);

	void save(DeliveryAutoCreateSetting setting);
	
	void update(DeliveryAutoCreateSetting setting);
	
	
	/************************************ for read only **************************************/
	
	DeliveryAutoCreateSetting readByType(String type);
}

