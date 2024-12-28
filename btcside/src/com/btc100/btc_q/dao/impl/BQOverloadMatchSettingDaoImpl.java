package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQOverloadMatchSettingDao;
import com.btc100.btc_q.entity.BQOverloadMatchSetting;

@Repository("bqOverloadMatchSettingDao")
public class BQOverloadMatchSettingDaoImpl<T> extends BaseDaoImpl<T> implements BQOverloadMatchSettingDao<T> {

	@Override
	public BQOverloadMatchSetting getSettingByCode(String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		final String hql = "from BQOverloadMatchSetting where f_code = :code";
		return (BQOverloadMatchSetting) get(hql, params);
	}
	
}
