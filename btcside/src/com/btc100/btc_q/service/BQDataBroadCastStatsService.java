package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQDataBroadCastStats;
import com.btc100.btc_q.vo.PagerModel;

public interface BQDataBroadCastStatsService {

	void save(BQDataBroadCastStats stats);

	BQDataBroadCastStats getByCodeAndTime(String code, long time);
	
	
	/************************************ for read only **************************************/
	
	BQDataBroadCastStats getLast(String code);
	
	PagerModel readBySymbol(String code, long startTime, long endTime);

}
