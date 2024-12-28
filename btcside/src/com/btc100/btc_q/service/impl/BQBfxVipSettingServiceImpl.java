package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQBfxVipSettingDao;
import com.btc100.btc_q.entity.BQBfxVipSetting;
import com.btc100.btc_q.service.BQBfxVipSettingService;

@Service("bqBfxVipSettingService")
public class BQBfxVipSettingServiceImpl implements BQBfxVipSettingService {

	@Autowired
	private BQBfxVipSettingDao<BQBfxVipSetting> bqBfxVipSettingDao;
	
	@Override
	public BQBfxVipSetting getBfxVipSettingById(long id) {
		return bqBfxVipSettingDao.get(BQBfxVipSetting.class, id);
	}
	
	@Override
	public BQBfxVipSetting readBfxVipSettingById(long id) {
		return bqBfxVipSettingDao.get(BQBfxVipSetting.class, id);
	}
	
	@Override
	public void save(BQBfxVipSetting ba) {
		bqBfxVipSettingDao.save(ba);
	}

	@Override
	public List<BQBfxVipSetting> findAll() {
		return bqBfxVipSettingDao.find("from BQBfxVipSetting where 1=1 order by hold_count desc");
	}
	
	@Override
	public List<BQBfxVipSetting> readAll() {
		return bqBfxVipSettingDao.find("from BQBfxVipSetting where 1=1 order by hold_count desc");
	}

	@Override
	public void update(BQBfxVipSetting setting) {
		bqBfxVipSettingDao.update(setting);
	}

	@Override
	public void delete(long id) {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		
		bqBfxVipSettingDao.deleteCondition("delete from BQBfxVipSetting where id = :id", params);
	}

}
