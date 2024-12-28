package com.btc100.btc_q.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.DeliveryOverloadMatchSettingDao;
import com.btc100.btc_q.entity.DeliveryOverloadMatchSetting;
import com.btc100.btc_q.service.DeliveryOverloadMatchSettingService;

@Service("DeliveryOverloadMatchSettingService")
public class DeliveryOverloadMatchSettingServiceImpl implements DeliveryOverloadMatchSettingService {

	@Autowired
	private DeliveryOverloadMatchSettingDao<DeliveryOverloadMatchSetting> deliveryOverloadMatchSettingDao;

	@Override
	public DeliveryOverloadMatchSetting getSettingByCode(String code) {
		return deliveryOverloadMatchSettingDao.getSettingByCode(code);
	}

	@Override
	public DeliveryOverloadMatchSetting readSettingByCode(String code) {
		return deliveryOverloadMatchSettingDao.getSettingByCode(code);
	}
	
	@Override
	public void save(DeliveryOverloadMatchSetting ba) {
		deliveryOverloadMatchSettingDao.save(ba);
	}

	@Override
	public void update(DeliveryOverloadMatchSetting setting) {
		deliveryOverloadMatchSettingDao.update(setting);
	}

}
