package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.BQExchangeSetting;

public interface BQExchangeSettingDao<T> extends BaseDaoI<T> {
	
	BQExchangeSetting getExchangeSettingBySymbol(String symbol);
}
