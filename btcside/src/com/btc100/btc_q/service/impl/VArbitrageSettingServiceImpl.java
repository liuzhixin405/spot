package com.btc100.btc_q.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.VArbitrageSettingDao;
import com.btc100.btc_q.entity.VArbitrageSetting;
import com.btc100.btc_q.service.VArbitrageSettingService;

@Service("vArbitrageSettingService")
public class VArbitrageSettingServiceImpl implements VArbitrageSettingService {

	@Autowired
	private VArbitrageSettingDao<VArbitrageSetting> vArbitrageSettingDao;

	@Override
	public VArbitrageSetting getArbitrageSettingByCode(String code) {
		return vArbitrageSettingDao.getArbitrageSettingByCode(code);
	}
	
	@Override
	public VArbitrageSetting readArbitrageSettingByCode(String code) {
		return vArbitrageSettingDao.getArbitrageSettingByCode(code);
	}

	@Override
	public void save(VArbitrageSetting ba) {
		vArbitrageSettingDao.save(ba);
	}

	@Override
	public void update(VArbitrageSetting setting) {
		vArbitrageSettingDao.update(setting);
	}

}
