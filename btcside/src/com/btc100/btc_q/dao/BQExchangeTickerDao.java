package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.BQExchangeTicker;

public interface BQExchangeTickerDao<T> extends BaseDaoI<T> {
	
	BQExchangeTicker getExchangeTickerByCurrencyType(int typeId);
}
