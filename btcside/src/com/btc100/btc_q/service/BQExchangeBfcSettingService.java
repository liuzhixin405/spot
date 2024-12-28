package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQExchangeBfcSetting;

public interface BQExchangeBfcSettingService {

	void save(BQExchangeBfcSetting setting);
	
	void update(BQExchangeBfcSetting setting);
	
	List<BQExchangeBfcSetting> findAll();
	
	
	/************************************ for read only **************************************/
	
	BQExchangeBfcSetting readBySymbol(String symbol);
	
	List<BQExchangeBfcSetting> readAll();
}

