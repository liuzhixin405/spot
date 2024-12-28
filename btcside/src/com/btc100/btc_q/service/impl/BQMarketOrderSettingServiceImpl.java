package com.btc100.btc_q.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQMarketOrderSettingDao;
import com.btc100.btc_q.entity.BQMarketOrderSetting;
import com.btc100.btc_q.service.BQMarketOrderSettingService;

@Service("bqMarketOrderSettingService")
public class BQMarketOrderSettingServiceImpl implements BQMarketOrderSettingService {

	@Autowired
	private BQMarketOrderSettingDao<BQMarketOrderSetting> bqSettingDao;

	@Override
	public void save(BQMarketOrderSetting ba) {
		bqSettingDao.save(ba);
	}

	@Override
	public void update(BQMarketOrderSetting setting) {
		bqSettingDao.update(setting);
	}

	@Override
	public BQMarketOrderSetting get() {
		return bqSettingDao.get("from BQMarketOrderSetting where 1=1");
	}

	@Override
	public BQMarketOrderSetting read() {
		return bqSettingDao.get("from BQMarketOrderSetting where 1=1");
	}

}
