package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQBuyBfxBackSetting;

public interface BQBuyBfxBackSettingService {

	void save(BQBuyBfxBackSetting setting);
	
	void update(BQBuyBfxBackSetting setting);
	
	List<BQBuyBfxBackSetting> findAll();
	
	
	/************************************ for read only **************************************/
	
	BQBuyBfxBackSetting readOne();
	
	List<BQBuyBfxBackSetting> readAll();
}

