package com.btc100.btc_q.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.DeliveryArbitrageSettingDao;
import com.btc100.btc_q.entity.DeliveryArbitrageSetting;
import com.btc100.btc_q.service.DeliveryArbitrageSettingService;

@Service("deliveryArbitrageSettingService")
public class DeliveryArbitrageSettingServiceImpl implements DeliveryArbitrageSettingService {

	@Autowired
	private DeliveryArbitrageSettingDao<DeliveryArbitrageSetting> deliveryArbitrageSettingDao;

	@Override
	public DeliveryArbitrageSetting getArbitrageSettingByCode(String code) {
		return deliveryArbitrageSettingDao.getArbitrageSettingByCode(code);
	}

	@Override
	public DeliveryArbitrageSetting readArbitrageSettingByCode(String code) {
		return deliveryArbitrageSettingDao.getArbitrageSettingByCode(code);
	}
	
	@Override
	public void save(DeliveryArbitrageSetting ba) {
		deliveryArbitrageSettingDao.save(ba);
	}

	@Override
	public void update(DeliveryArbitrageSetting setting) {
		deliveryArbitrageSettingDao.update(setting);
	}

}
