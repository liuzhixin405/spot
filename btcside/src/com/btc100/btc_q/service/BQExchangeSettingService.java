package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQExchangeSetting;

public interface BQExchangeSettingService {

	void save(BQExchangeSetting setting);
	
	void update(BQExchangeSetting setting);
	
	List<BQExchangeSetting> findAll();
	
	
	/************************************ for read only **************************************/
	
	BQExchangeSetting readExchangeSettingBySymbol(String symbol);
	
	List<BQExchangeSetting> readAll();
}

