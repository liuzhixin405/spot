package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.VOverloadMatchSettingDao;
import com.btc100.btc_q.entity.VOverloadMatchSetting;

@Repository("vOverloadMatchSettingDao")
public class VOverloadMatchSettingDaoImpl<T> extends BaseDaoImpl<T> implements VOverloadMatchSettingDao<T> {

	@Override
	public VOverloadMatchSetting getSettingByCode(String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		final String hql = "from VOverloadMatchSetting where f_code = :code";
		return (VOverloadMatchSetting) get(hql, params);
	}
	
}
