package com.btc100.btc_q.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQSpotArbitrageSettingDao;
import com.btc100.btc_q.entity.BQSpotArbitrageSetting;
import com.btc100.btc_q.service.BQSpotArbitrageSettingService;

@Service("bqSpotArbitrageSettingService")
public class BQSpotArbitrageSettingServiceImpl implements BQSpotArbitrageSettingService {

	@Autowired
	private BQSpotArbitrageSettingDao<BQSpotArbitrageSetting> bqSpotArbitrageSettingDao;

	@Override
	public BQSpotArbitrageSetting getArbitrageSettingByCode(String code) {
		return bqSpotArbitrageSettingDao.getArbitrageSettingByCode(code);
	}
	
	@Override
	public BQSpotArbitrageSetting readArbitrageSettingByCode(String code) {
		return bqSpotArbitrageSettingDao.getArbitrageSettingByCode(code);
	}

	@Override
	public void save(BQSpotArbitrageSetting ba) {
		bqSpotArbitrageSettingDao.save(ba);
	}

	@Override
	public void update(BQSpotArbitrageSetting setting) {
		bqSpotArbitrageSettingDao.update(setting);
	}

}
