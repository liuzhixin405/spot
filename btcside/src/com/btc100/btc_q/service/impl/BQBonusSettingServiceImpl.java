package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQBonusSettingDao;
import com.btc100.btc_q.entity.BQBonusSetting;
import com.btc100.btc_q.service.BQBonusSettingService;

@Service("bqBonusSettingService")
public class BQBonusSettingServiceImpl implements BQBonusSettingService {

	@Autowired
	private BQBonusSettingDao<BQBonusSetting> bqBonusSetting;

	@Override
	public void save(BQBonusSetting ba) {
		bqBonusSetting.save(ba);
	}

	@Override
	public BQBonusSetting get() {
		return bqBonusSetting.get("from BQBonusSetting where 1=1");
	}

	@Override
	public BQBonusSetting readOne() {
		return bqBonusSetting.get("from BQBonusSetting where 1=1");
	}
	
	@Override
	public void update(BQBonusSetting setting) {
		String hql = "UPDATE BQBonusSetting b SET b.running=:running,b.begin_time=:begin_time WHERE b.id=:id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", setting.getId());
		map.put("running", setting.getRunning());
		map.put("begin_time", setting.getBegin_time());
		bqBonusSetting.updateCondition(hql, map);
	}

	@Override
	public void firstDone(long id) {
		String hql = "UPDATE BQBonusSetting b SET b.first_done=:firstDone WHERE b.id=:id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("firstDone", true);
		bqBonusSetting.updateCondition(hql, map);
		
	}

}
