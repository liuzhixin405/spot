package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.DeliveryArbitrageSettingDao;
import com.btc100.btc_q.entity.DeliveryArbitrageSetting;

@Repository("deliveryArbitrageSettingDao")
public class DeliveryArbitrageSettingDaoImpl<T> extends BaseDaoImpl<T> implements DeliveryArbitrageSettingDao<T> {

	@Override
	public DeliveryArbitrageSetting getArbitrageSettingByCode(String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		final String hql = "from DeliveryArbitrageSetting where f_code = :code";
		return (DeliveryArbitrageSetting) get(hql, params);
	}
	
}
