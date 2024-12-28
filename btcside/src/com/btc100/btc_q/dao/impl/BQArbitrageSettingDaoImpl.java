package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQArbitrageSettingDao;
import com.btc100.btc_q.entity.BQArbitrageSetting;

@Repository("bqArbitrageSettingDao")
public class BQArbitrageSettingDaoImpl<T> extends BaseDaoImpl<T> implements BQArbitrageSettingDao<T> {

	@Override
	public BQArbitrageSetting getArbitrageSettingByCode(String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		final String hql = "from BQArbitrageSetting where f_code = :code";
		return (BQArbitrageSetting) get(hql, params);
	}
	
}
