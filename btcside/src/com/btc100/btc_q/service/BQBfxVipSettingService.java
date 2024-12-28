package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQBfxVipSetting;

public interface BQBfxVipSettingService {

	void save(BQBfxVipSetting setting);
	
	void delete(long id);
	
	void update(BQBfxVipSetting setting);
	
	List<BQBfxVipSetting> findAll();
	
	BQBfxVipSetting getBfxVipSettingById(long id);
	
	
	/************************************ for read only **************************************/
	
	BQBfxVipSetting readBfxVipSettingById(long id);
	
	List<BQBfxVipSetting> readAll();
}

