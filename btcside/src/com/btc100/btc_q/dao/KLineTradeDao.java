package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.KLineTrade;

public interface KLineTradeDao extends BaseDaoI<KLineTrade> {
	
	KLineTrade insert(KLineTrade trade, String timeType);

	void update(KLineTrade trade, String timeType);
	
	KLineTrade getAtTime(String code, String timeType, long time);
}
