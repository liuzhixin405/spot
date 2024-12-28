package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQIeoUnlockSettingDao;
import com.btc100.btc_q.entity.BQIeoUnlockSetting;
import com.btc100.btc_q.service.BQIeoUnlockSettingService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqIeoUnlockSettingService")
public class BQIeoUnlockSettingServiceImpl implements BQIeoUnlockSettingService {
	
	@Autowired
	private  BQIeoUnlockSettingDao<BQIeoUnlockSetting> bqIeoUnlockSettingDao;

	@Override
	public void save(BQIeoUnlockSetting ba) {
		bqIeoUnlockSettingDao.save(ba);
	}

	@Override
	public void update(BQIeoUnlockSetting setting) {
		bqIeoUnlockSettingDao.update(setting);
	}

	@Override
	public void delete(long id) {
		String hql = "delete from BQIeoUnlockSetting where id = :id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		
		bqIeoUnlockSettingDao.deleteCondition(hql, params);
	}
	
	@Override
	public PagerModel readAll() {
		String hql = "from BQIeoUnlockSetting";
		return bqIeoUnlockSettingDao.searchHql(hql);
	}

	@Override
	public BQIeoUnlockSetting readById(long id) {
		return bqIeoUnlockSettingDao.get(BQIeoUnlockSetting.class, id);
	}

	@Override
	public BQIeoUnlockSetting getByTime(long time) {
		String hql = "from BQIeoUnlockSetting where unlock_time = :time";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("time", time);
		return bqIeoUnlockSettingDao.get(hql, params);
	}
}
