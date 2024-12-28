package com.btc100.btc_q.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQExchangeSettingDao;
import com.btc100.btc_q.entity.BQExchangeSetting;
import com.btc100.btc_q.service.BQExchangeSettingService;

@Service("bqExchangeSettingService")
public class BQExchangeSettingServiceImpl implements BQExchangeSettingService {

	@Autowired
	private BQExchangeSettingDao<BQExchangeSetting> bqExchangeSettingDao;

	@Override
	public BQExchangeSetting readExchangeSettingBySymbol(String symbol) {
		return bqExchangeSettingDao.getExchangeSettingBySymbol(symbol);
	}

	@Override
	public void save(BQExchangeSetting ba) {
		bqExchangeSettingDao.save(ba);
	}

	@Override
	public List<BQExchangeSetting> findAll() {
		return bqExchangeSettingDao.find("from BQExchangeSetting where 1=1");
	}

	@Override
	public void update(BQExchangeSetting setting) {
		bqExchangeSettingDao.update(setting);
	}
	
	@Override
	public List<BQExchangeSetting> readAll() {
		return bqExchangeSettingDao.find("from BQExchangeSetting where 1=1");
	}

}
