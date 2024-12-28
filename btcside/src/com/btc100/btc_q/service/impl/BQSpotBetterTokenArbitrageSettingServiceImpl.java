package com.btc100.btc_q.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQSpotBetterTokenArbitrageSettingDao;
import com.btc100.btc_q.entity.BQSpotBetterTokenArbitrageSetting;
import com.btc100.btc_q.service.BQSpotBetterTokenArbitrageSettingService;

@Service("bqSpotBetterTokenArbitrageSettingService")
public class BQSpotBetterTokenArbitrageSettingServiceImpl implements BQSpotBetterTokenArbitrageSettingService {

	@Autowired
	private BQSpotBetterTokenArbitrageSettingDao<BQSpotBetterTokenArbitrageSetting> bqArbitrageSettingDao;

	@Override
	public BQSpotBetterTokenArbitrageSetting getArbitrageSetting() {
		return bqArbitrageSettingDao.get("from BQSpotBetterTokenArbitrageSetting where 1=1");
	}

	@Override
	public BQSpotBetterTokenArbitrageSetting readArbitrageSetting() {
		return bqArbitrageSettingDao.get("from BQSpotBetterTokenArbitrageSetting where 1=1");
	}
	
	@Override
	public void save(BQSpotBetterTokenArbitrageSetting ba) {
		bqArbitrageSettingDao.save(ba);
	}

	@Override
	public void update(BQSpotBetterTokenArbitrageSetting setting) {
		bqArbitrageSettingDao.update(setting);
	}

}
