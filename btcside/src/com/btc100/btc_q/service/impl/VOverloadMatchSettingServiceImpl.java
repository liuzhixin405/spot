package com.btc100.btc_q.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.VOverloadMatchSettingDao;
import com.btc100.btc_q.entity.VOverloadMatchSetting;
import com.btc100.btc_q.service.VOverloadMatchSettingService;

@Service("vOverloadMatchSettingService")
public class VOverloadMatchSettingServiceImpl implements VOverloadMatchSettingService {

	@Autowired
	private VOverloadMatchSettingDao<VOverloadMatchSetting> vOverloadMatchSettingDao;

	@Override
	public VOverloadMatchSetting getSettingByCode(String code) {
		return vOverloadMatchSettingDao.getSettingByCode(code);
	}

	@Override
	public VOverloadMatchSetting readSettingByCode(String code) {
		return vOverloadMatchSettingDao.getSettingByCode(code);
	}
	
	@Override
	public void save(VOverloadMatchSetting ba) {
		vOverloadMatchSettingDao.save(ba);
	}

	@Override
	public void update(VOverloadMatchSetting setting) {
		vOverloadMatchSettingDao.update(setting);
	}

}
