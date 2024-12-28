package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.VTradeDayStats;

public interface VTradeDayStatsService {

	void saveOrUpdate(VTradeDayStats o);

	VTradeDayStats getLastBeforeTimeByUserId(long userId, long beforeTime);
}
