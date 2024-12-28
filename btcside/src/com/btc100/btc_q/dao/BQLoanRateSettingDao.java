package com.btc100.btc_q.dao;

import java.util.List;

public interface BQLoanRateSettingDao<T> extends BaseDaoI<T> {
	
	T getLoanRateSettingByIdAndCycle(long id, int cycle);
	
	List<T> findAllById(long id);
	
}
