package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.NStatisticalForm;

public interface NStatisticalFormDao<T> extends BaseDaoI<T> {
	
	int insert(String partition,  NStatisticalForm form);

}
