package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.NStatisticalHourlyForm;

public interface NStatisticalHourlyFormService {

	void save(String partition, NStatisticalHourlyForm form);
	
	List<NStatisticalHourlyForm> findStatisticalFormByTime(String partition, long time);	
	
	NStatisticalHourlyForm getByCodeAndTime(String partition, String code, long time);
}
