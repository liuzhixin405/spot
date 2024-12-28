package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.DeliveryOverloadMatchSettingDao;
import com.btc100.btc_q.entity.DeliveryOverloadMatchSetting;

@Repository("deliveryOverloadMatchSettingDao")
public class DeliveryOverloadMatchSettingDaoImpl<T> extends BaseDaoImpl<T> implements DeliveryOverloadMatchSettingDao<T> {

	@Override
	public DeliveryOverloadMatchSetting getSettingByCode(String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		final String hql = "from DeliveryOverloadMatchSetting where f_code = :code";
		return (DeliveryOverloadMatchSetting) get(hql, params);
	}
	
}
