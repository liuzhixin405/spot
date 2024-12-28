package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.NOverloadMatchSetting;

public interface NOverloadMatchSettingService {

	NOverloadMatchSetting getSettingByCode(String partition, String code);

	void save(String partition, NOverloadMatchSetting setting);
	
	void update(String partition, NOverloadMatchSetting setting);
	
	
	/************************************ for read only **************************************/
	
	NOverloadMatchSetting readSettingByCode(String partition, String code);
}

