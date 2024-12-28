package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.NTradeLogReconcilia;

public interface NTradeLogReconciliaService {

	void save(String partition, NTradeLogReconcilia re);
	
}
