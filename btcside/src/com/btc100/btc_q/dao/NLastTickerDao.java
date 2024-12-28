package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.NLastTicker;

public interface NLastTickerDao<T> extends BaseDaoI<T> {
	
	int insert(String partition, NLastTicker ticker);
	
	int update(String partition, NLastTicker ticker);
	
}
