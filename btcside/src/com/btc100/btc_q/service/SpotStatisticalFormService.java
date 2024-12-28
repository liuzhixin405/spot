package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.SpotStatisticalForm;

public interface SpotStatisticalFormService {

	void save(SpotStatisticalForm statisticalForm);
	
	List<SpotStatisticalForm> findStatisticalFormByTime(long time);
}
