package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQDataBroadCastSetting;

public interface BQDataBroadCastSettingService {

	BQDataBroadCastSetting getByCode(String code);

	void save(BQDataBroadCastSetting setting);

	void update(BQDataBroadCastSetting setting);

	
	/************************************ for read only **************************************/
	
	List<BQDataBroadCastSetting> readAll();
}
