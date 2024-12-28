package com.btc100.btc_q.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQBuyBfxBackSettingDao;
import com.btc100.btc_q.entity.BQBuyBfxBackSetting;
import com.btc100.btc_q.service.BQBuyBfxBackSettingService;

@Service("bqBuyBfxBackSettingService")
public class BQBuyBfxBackSettingServiceImpl implements BQBuyBfxBackSettingService {

	@Autowired
	private BQBuyBfxBackSettingDao<BQBuyBfxBackSetting> bqExchangeSettingDao;

	@Override
	public BQBuyBfxBackSetting readOne() {
		return bqExchangeSettingDao.get("from BQBuyBfxBackSetting where 1=1");
	}

	@Override
	public void save(BQBuyBfxBackSetting ba) {
		bqExchangeSettingDao.save(ba);
	}

	@Override
	public List<BQBuyBfxBackSetting> findAll() {
		return bqExchangeSettingDao.find("from BQBuyBfxBackSetting where 1=1");
	}

	@Override
	public void update(BQBuyBfxBackSetting setting) {
		bqExchangeSettingDao.update(setting);
	}
	
	@Override
	public List<BQBuyBfxBackSetting> readAll() {
		return bqExchangeSettingDao.find("from BQBuyBfxBackSetting where 1=1");
	}

}
