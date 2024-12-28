package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQExchangeBfcSettingDao;
import com.btc100.btc_q.entity.BQExchangeBfcSetting;
import com.btc100.btc_q.service.BQExchangeBfcSettingService;

@Service("bqExchangeBfcSettingService")
public class BQExchangeBfcSettingServiceImpl implements BQExchangeBfcSettingService {

	@Autowired
	private BQExchangeBfcSettingDao<BQExchangeBfcSetting> bqExchangeSettingDao;

	@Override
	public void save(BQExchangeBfcSetting setting) {
		bqExchangeSettingDao.save(setting);
	}

	@Override
	public void update(BQExchangeBfcSetting setting) {
		if (setting.getId() == null || setting.getId() <= 0) {
			bqExchangeSettingDao.save(setting);
			return;
		}
		bqExchangeSettingDao.update(setting);
	}

	@Override
	public List<BQExchangeBfcSetting> findAll() {
		return bqExchangeSettingDao.find("from BQExchangeBfcSetting where 1=1");
	}

	@Override
	public BQExchangeBfcSetting readBySymbol(String symbol) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("symbol", symbol);
		final String hql = "from BQExchangeBfcSetting where symbol = :symbol";
		return bqExchangeSettingDao.get(hql, params);
	}

	@Override
	public List<BQExchangeBfcSetting> readAll() {
		return bqExchangeSettingDao.find("from BQExchangeBfcSetting where 1=1");
	}

}
