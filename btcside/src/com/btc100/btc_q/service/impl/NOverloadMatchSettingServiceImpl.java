package com.btc100.btc_q.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.NOverloadMatchSettingDao;
import com.btc100.btc_q.entity.NOverloadMatchSetting;
import com.btc100.btc_q.service.NOverloadMatchSettingService;

@Service("nOverloadMatchSettingService")
public class NOverloadMatchSettingServiceImpl implements NOverloadMatchSettingService {

	@Autowired
	private NOverloadMatchSettingDao<NOverloadMatchSetting> nOverloadMatchSettingDao;

	@Override
	public NOverloadMatchSetting getSettingByCode(String partition, String code) {
		return nOverloadMatchSettingDao.getSettingByCode(partition, code);
	}

	@Override
	public NOverloadMatchSetting readSettingByCode(String partition, String code) {
		return nOverloadMatchSettingDao.getSettingByCode(partition, code);
	}
	
	@Override
	public void save(String partition, NOverloadMatchSetting ba) {
		nOverloadMatchSettingDao.insert(partition, ba);
	}

	@Override
	public void update(String partition, NOverloadMatchSetting setting) {
		nOverloadMatchSettingDao.update(partition, setting);
	}

}
