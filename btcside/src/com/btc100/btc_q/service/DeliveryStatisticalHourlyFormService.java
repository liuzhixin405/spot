package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.DeliveryStatisticalHourlyForm;

public interface DeliveryStatisticalHourlyFormService {

	void save(DeliveryStatisticalHourlyForm form);
	
	List<DeliveryStatisticalHourlyForm> findStatisticalFormByTime(long time);
}
