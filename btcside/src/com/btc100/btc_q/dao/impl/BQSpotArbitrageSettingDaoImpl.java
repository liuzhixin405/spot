package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQSpotArbitrageSettingDao;
import com.btc100.btc_q.entity.BQSpotArbitrageSetting;

@Repository("bqSpotArbitrageSettingDao")
public class BQSpotArbitrageSettingDaoImpl<T> extends BaseDaoImpl<T> implements BQSpotArbitrageSettingDao<T> {

	@Override
	public BQSpotArbitrageSetting getArbitrageSettingByCode(String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		final String hql = "from BQSpotArbitrageSetting where f_code = :code";
		return (BQSpotArbitrageSetting) get(hql, params);
	}
	
}
