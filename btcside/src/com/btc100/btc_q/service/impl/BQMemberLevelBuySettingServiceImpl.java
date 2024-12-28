package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQMemberLevelBuySettingDao;
import com.btc100.btc_q.entity.BQMemberLevelBuySetting;
import com.btc100.btc_q.service.BQMemberLevelBuySettingService;

@Service("bqMemberLevelBuySettingService")
public class BQMemberLevelBuySettingServiceImpl implements BQMemberLevelBuySettingService {
	
	@Autowired
	private  BQMemberLevelBuySettingDao<BQMemberLevelBuySetting> bqIeoUnlockSettingDao;

	@Override
	public void save(BQMemberLevelBuySetting ba) {
		bqIeoUnlockSettingDao.save(ba);
	}

	@Override
	public void update(BQMemberLevelBuySetting setting) {
		bqIeoUnlockSettingDao.update(setting);
	}

	@Override
	public void delete(long id) {
		String hql = "delete from BQMemberLevelBuySetting where id = :id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		
		bqIeoUnlockSettingDao.deleteCondition(hql, params);
	}
	
	@Override
	public BQMemberLevelBuySetting readById() {
		return bqIeoUnlockSettingDao.get("from BQMemberLevelBuySetting");
	}

	@Override
	public BQMemberLevelBuySetting get() {
		return bqIeoUnlockSettingDao.get("from BQMemberLevelBuySetting");
	}

}
