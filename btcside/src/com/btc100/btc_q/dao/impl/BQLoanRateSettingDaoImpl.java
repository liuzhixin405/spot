package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQLoanRateSettingDao;

@Repository("bqLoanRateSettingDao")
public class BQLoanRateSettingDaoImpl<T> extends BaseDaoImpl<T> implements BQLoanRateSettingDao<T> {
	
	@Override
	public T getLoanRateSettingByIdAndCycle(long id, int cycle) {
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		params.put("cycle", cycle);
		
		return get("from BQLoanRateSetting where loan_setting_id=:id and cycle=:cycle", params);
	}

	@Override
	public List<T> findAllById(long id) {
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		
		return find("from BQLoanRateSetting where loan_setting_id=:id", params);
	}
}
