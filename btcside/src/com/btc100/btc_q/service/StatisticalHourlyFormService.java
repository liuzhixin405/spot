package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.StatisticalHourlyForm;

public interface StatisticalHourlyFormService {

	void save(StatisticalHourlyForm form);
	
	List<StatisticalHourlyForm> findStatisticalFormByTime(long time);
}
