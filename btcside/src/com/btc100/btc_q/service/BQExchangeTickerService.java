package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQExchangeTicker;
import com.btc100.btc_q.vo.TickerVo;

public interface BQExchangeTickerService {

	BQExchangeTicker getExchangeTickerByCurrencyType(int type);

	void saveOrUpdate(TickerVo vo);
	
	void update(BQExchangeTicker setting);
	
	
	/************************************ for read only **************************************/
	
	BQExchangeTicker readExchangeTickerByCurrencyType(int type);
}

