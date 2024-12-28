package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.VStatisticalForm;

public interface VStatisticalFormService {

	void save(VStatisticalForm statisticalForm);
	
	List<VStatisticalForm> findStatisticalFormByTime(long time);
	
	VStatisticalForm getByCodeAndTime(String code, long time);
}
