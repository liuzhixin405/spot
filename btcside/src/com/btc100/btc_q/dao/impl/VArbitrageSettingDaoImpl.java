package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.VArbitrageSettingDao;
import com.btc100.btc_q.entity.VArbitrageSetting;

@Repository("vArbitrageSettingDao")
public class VArbitrageSettingDaoImpl<T> extends BaseDaoImpl<T> implements VArbitrageSettingDao<T> {

	@Override
	public VArbitrageSetting getArbitrageSettingByCode(String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		final String hql = "from VArbitrageSetting where f_code = :code";
		return (VArbitrageSetting) get(hql, params);
	}
	
}
