package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.VOverloadMatchSetting;

public interface VOverloadMatchSettingService {

	VOverloadMatchSetting getSettingByCode(String code);

	void save(VOverloadMatchSetting setting);
	
	void update(VOverloadMatchSetting setting);
	
	
	/************************************ for read only **************************************/
	
	VOverloadMatchSetting readSettingByCode(String code);
}

