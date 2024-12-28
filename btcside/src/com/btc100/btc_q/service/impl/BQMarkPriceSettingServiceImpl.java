package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQMarkPriceSettingDao;
import com.btc100.btc_q.entity.BQMarkPriceSetting;
import com.btc100.btc_q.service.BQMarkPriceSettingService;
import com.btc100.btc_q.vo.PagerModel;
	
@Service("bqMarkPriceSettingService")
public class BQMarkPriceSettingServiceImpl implements BQMarkPriceSettingService {
	
	@Autowired
	private BQMarkPriceSettingDao<BQMarkPriceSetting> bqMarkPriceSettingDao;

	@Override
	public void save(BQMarkPriceSetting setting) {
		bqMarkPriceSettingDao.save(setting);
	}

	@Override
	public void update(BQMarkPriceSetting setting) {
		bqMarkPriceSettingDao.update(setting);
	}
	
	@Override
	public void delete(Long id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		bqMarkPriceSettingDao.deleteCondition("delete BQMarkPriceSetting where id = :id", params);
	}

	@Override
	public BQMarkPriceSetting get() {
		return bqMarkPriceSettingDao.get("from BQMarkPriceSetting where code = ''");
	}

	@Override
	public BQMarkPriceSetting readOne() {
		return bqMarkPriceSettingDao.get("from BQMarkPriceSetting where code = ''");
	}

	@Override
	public BQMarkPriceSetting getByCode(String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return bqMarkPriceSettingDao.get("from BQMarkPriceSetting where code = :code", params);
	}

	@Override
	public BQMarkPriceSetting readByCode(String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return bqMarkPriceSettingDao.get("from BQMarkPriceSetting where code = :code", params);
	}

	@Override
	public PagerModel readList() {
		return bqMarkPriceSettingDao.searchHql("from BQMarkPriceSetting");
	}

}
