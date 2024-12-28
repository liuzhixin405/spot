package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.VTradeWeekStats;

public interface VTradeWeekStatsService {

	void saveOrUpdate(VTradeWeekStats o);


	
	///************************************ for read only **************************************/

	List<VTradeWeekStats> readAll(long recordTime, int limit);
	
}
