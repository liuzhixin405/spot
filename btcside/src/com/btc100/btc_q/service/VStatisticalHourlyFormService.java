package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.VStatisticalHourlyForm;

public interface VStatisticalHourlyFormService {

	void save(VStatisticalHourlyForm form);
	
	List<VStatisticalHourlyForm> findStatisticalFormByTime(long time);	
	
	VStatisticalHourlyForm getByCodeAndTime(String code, long time);
}
