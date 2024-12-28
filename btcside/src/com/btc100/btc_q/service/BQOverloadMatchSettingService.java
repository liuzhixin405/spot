package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQOverloadMatchSetting;

public interface BQOverloadMatchSettingService {

	BQOverloadMatchSetting getSettingByCode(String code);

	void save(BQOverloadMatchSetting setting);
	
	void update(BQOverloadMatchSetting setting);
	
	
	/************************************ for read only **************************************/
	
	BQOverloadMatchSetting readSettingByCode(String code);
}

