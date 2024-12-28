package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.NStatisticalHourlyForm;

public interface NStatisticalHourlyFormDao<T> extends BaseDaoI<T> {

	int insert(String partition,  NStatisticalHourlyForm form);
	
}
