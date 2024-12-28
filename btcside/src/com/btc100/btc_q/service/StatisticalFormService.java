package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.StatisticalForm;

public interface StatisticalFormService {

	void save(StatisticalForm statisticalForm);
	
	List<StatisticalForm> findStatisticalFormByTime(long time);
}
