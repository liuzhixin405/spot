package com.btc100.btc_q.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQBfxFeeSettingDao;
import com.btc100.btc_q.entity.BQBfxFeeSetting;
import com.btc100.btc_q.service.BQBfxFeeSettingService;

@Service("bqBfxFeeSettingService")
public class BQBfxFeeSettingServiceImpl implements BQBfxFeeSettingService {

	@Autowired
	private BQBfxFeeSettingDao<BQBfxFeeSetting> bqBfxFeeSettingDao;
	
	@Override
	public void save(BQBfxFeeSetting ba) {
		bqBfxFeeSettingDao.save(ba);
	}

	@Override
	public BQBfxFeeSetting get() {
		return bqBfxFeeSettingDao.get("from BQBfxFeeSetting order by id desc");
	}

	@Override
	public BQBfxFeeSetting readOne() {
		return bqBfxFeeSettingDao.get("from BQBfxFeeSetting order by id desc");
	}
	
	@Override
	public void update(BQBfxFeeSetting setting) {
		bqBfxFeeSettingDao.update(setting);
	}

}
