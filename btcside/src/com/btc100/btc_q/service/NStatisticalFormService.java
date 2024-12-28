package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.NStatisticalForm;

public interface NStatisticalFormService {

	void save(String partition, NStatisticalForm statisticalForm);
	
	List<NStatisticalForm> findStatisticalFormByTime(String partition, long time);
	
	NStatisticalForm getByCodeAndTime(String partition, String code, long time);
}
