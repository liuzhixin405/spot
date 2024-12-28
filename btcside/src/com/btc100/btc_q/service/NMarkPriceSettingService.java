package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.NMarkPriceSetting;
import com.btc100.btc_q.vo.PagerModel;

public interface NMarkPriceSettingService {

	void save(String partition, NMarkPriceSetting setting);
	
	void update(String partition, NMarkPriceSetting setting);
	
	void delete(String partition, Long id);
	
	NMarkPriceSetting get(String partition);
	
	NMarkPriceSetting getByCode(String partition, String code);
	
	
	/************************************ for read only **************************************/
	
	NMarkPriceSetting readOne(String partition);
	
	NMarkPriceSetting readByCode(String partition, String code);
	
	PagerModel readList(String partition);
}
