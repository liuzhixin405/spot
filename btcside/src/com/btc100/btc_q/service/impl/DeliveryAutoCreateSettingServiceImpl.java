package com.btc100.btc_q.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.DeliveryAutoCreateSettingDao;
import com.btc100.btc_q.entity.DeliveryAutoCreateSetting;
import com.btc100.btc_q.service.DeliveryAutoCreateSettingService;

@Service("deliveryAutoCreateSettingService")
public class DeliveryAutoCreateSettingServiceImpl implements DeliveryAutoCreateSettingService {

	@Autowired
	private DeliveryAutoCreateSettingDao<DeliveryAutoCreateSetting> DeliveryAutoCreateSettingDao;

	@Override
	public DeliveryAutoCreateSetting getByType(String type) {
		return DeliveryAutoCreateSettingDao.getByType(type);
	}

	@Override
	public DeliveryAutoCreateSetting readByType(String type) {
		return DeliveryAutoCreateSettingDao.getByType(type);
	}
	
	@Override
	public void save(DeliveryAutoCreateSetting setting) {
		DeliveryAutoCreateSettingDao.save(setting);
	}

	@Override
	public void update(DeliveryAutoCreateSetting setting) {
		DeliveryAutoCreateSettingDao.update(setting);
	}

}
