package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQMarkPriceSetting;
import com.btc100.btc_q.vo.PagerModel;

public interface BQMarkPriceSettingService {

	void save(BQMarkPriceSetting setting);
	
	void update(BQMarkPriceSetting setting);
	
	void delete(Long id);
	
	BQMarkPriceSetting get();
	
	BQMarkPriceSetting getByCode(String code);
	
	
	/************************************ for read only **************************************/
	
	BQMarkPriceSetting readOne();
	
	BQMarkPriceSetting readByCode(String code);
	
	PagerModel readList();
}
