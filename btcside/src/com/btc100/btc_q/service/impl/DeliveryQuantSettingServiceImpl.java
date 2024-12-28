package com.btc100.btc_q.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.DeliveryQuantSettingDao;
import com.btc100.btc_q.entity.DeliveryQuantSetting;
import com.btc100.btc_q.service.DeliveryQuantSettingService;

@Service("deliveryQuantSettingService")
public class DeliveryQuantSettingServiceImpl implements DeliveryQuantSettingService {

	@Autowired
	private DeliveryQuantSettingDao<DeliveryQuantSetting> dao;

	@Override
	public DeliveryQuantSetting get() {
		String hql = "from DeliveryQuantSetting";
		List<DeliveryQuantSetting> list = dao.find(hql);
		if (list.size() > 0) {
			return list.get(0);
		}
		
		return null;
	}

	@Override
	public void update(DeliveryQuantSetting o) {
		if (o.getId() == null || o.getId() == 0) {
			o.setId(null);
			dao.save(o);
		} else {
			dao.update(o);
		}
	}
}
