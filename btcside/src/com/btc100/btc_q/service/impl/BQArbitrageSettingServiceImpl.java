package com.btc100.btc_q.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQArbitrageSettingDao;
import com.btc100.btc_q.entity.BQArbitrageSetting;
import com.btc100.btc_q.service.BQArbitrageSettingService;

@Service("bqArbitrageSettingService")
public class BQArbitrageSettingServiceImpl implements BQArbitrageSettingService {

	@Autowired
	private BQArbitrageSettingDao<BQArbitrageSetting> bqArbitrageSettingDao;

	@Override
	public BQArbitrageSetting getArbitrageSettingByCode(String code) {
		return bqArbitrageSettingDao.getArbitrageSettingByCode(code);
	}

	@Override
	public BQArbitrageSetting readArbitrageSettingByCode(String code) {
		return bqArbitrageSettingDao.getArbitrageSettingByCode(code);
	}
	
	@Override
	public void save(BQArbitrageSetting ba) {
		bqArbitrageSettingDao.save(ba);
	}

	@Override
	public void update(BQArbitrageSetting setting) {
		bqArbitrageSettingDao.update(setting);
	}

}
