package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.DeliveryAutoCreateSettingDao;
import com.btc100.btc_q.entity.DeliveryAutoCreateSetting;

@Repository("deliveryAutoCreateSettingDao")
public class DeliveryAutoCreateSettingDaoImpl<T> extends BaseDaoImpl<T> implements DeliveryAutoCreateSettingDao<T> {

	@Override
	public DeliveryAutoCreateSetting getByType(String type) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", type);
		final String hql = "from DeliveryAutoCreateSetting where delivery_type = :type";
		return (DeliveryAutoCreateSetting) get(hql, params);
	}

}
