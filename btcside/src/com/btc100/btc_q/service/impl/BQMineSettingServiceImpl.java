package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQMineSettingDao;
import com.btc100.btc_q.entity.BQMineSetting;
import com.btc100.btc_q.service.BQMineSettingService;

@Service("bqMineSettingService")
public class BQMineSettingServiceImpl implements BQMineSettingService {

	@Autowired
	private BQMineSettingDao<BQMineSetting> bqMineSetting;

	@Override
	public void save(BQMineSetting ba) {
		bqMineSetting.save(ba);
	}

	@Override
	public BQMineSetting get() {
		return bqMineSetting.get("from BQMineSetting");
	}
	
	@Override
	public BQMineSetting read() {
		return bqMineSetting.get("from BQMineSetting");
	}

	@Override
	public void update(BQMineSetting setting) {
		String hql = "UPDATE BQMineSetting b SET b.currency_type_id=:currencyId,b.total_amount=:totalAmount," +
				"b.running=:running,b.rebate=:rebate,b.begin_time=:beginTime,b.personal_max_amount=:personalMaxAmount WHERE b.id=:id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", setting.getId());
		map.put("currencyId", setting.getCurrency_type_id());
		map.put("totalAmount", setting.getTotal_amount());
		map.put("running", setting.getRunning());
		map.put("rebate", setting.getRebate());
		map.put("beginTime", setting.getBegin_time());
		map.put("personalMaxAmount", setting.getPersonal_max_amount());
		bqMineSetting.updateCondition(hql, map);
	}

}
