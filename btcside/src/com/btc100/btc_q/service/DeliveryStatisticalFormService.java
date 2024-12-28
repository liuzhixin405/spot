package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.DeliveryStatisticalForm;

public interface DeliveryStatisticalFormService {

	void save(DeliveryStatisticalForm statisticalForm);
	
	List<DeliveryStatisticalForm> findStatisticalFormByTime(long time);
}
