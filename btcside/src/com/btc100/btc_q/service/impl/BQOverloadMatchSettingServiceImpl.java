package com.btc100.btc_q.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQOverloadMatchSettingDao;
import com.btc100.btc_q.entity.BQOverloadMatchSetting;
import com.btc100.btc_q.service.BQOverloadMatchSettingService;

@Service("bqOverloadMatchSettingService")
public class BQOverloadMatchSettingServiceImpl implements BQOverloadMatchSettingService {

	@Autowired
	private BQOverloadMatchSettingDao<BQOverloadMatchSetting> bqOverloadMatchSettingDao;

	@Override
	public BQOverloadMatchSetting getSettingByCode(String code) {
		return bqOverloadMatchSettingDao.getSettingByCode(code);
	}

	@Override
	public BQOverloadMatchSetting readSettingByCode(String code) {
		return bqOverloadMatchSettingDao.getSettingByCode(code);
	}
	
	@Override
	public void save(BQOverloadMatchSetting ba) {
		bqOverloadMatchSettingDao.save(ba);
	}

	@Override
	public void update(BQOverloadMatchSetting setting) {
		bqOverloadMatchSettingDao.update(setting);
	}

}
