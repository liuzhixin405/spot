package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQQuantSettingDao;
import com.btc100.btc_q.entity.BQQuantSetting;
import com.btc100.btc_q.service.BQQuantSettingService;

@Service("bqQuantSettingService")
public class BQQuantSettingServiceImpl implements BQQuantSettingService {

	@Autowired
	private BQQuantSettingDao<BQQuantSetting> bqQuantSettingDao;

	@Override
	public BQQuantSetting get(String partition) {
		String hql = "from BQQuantSetting where part=:partition";
		Map<String, Object> parms = new HashMap<String, Object>();
		parms.put("partition", partition);
		return bqQuantSettingDao.get(hql, parms);
	}

	@Override
	public void update(BQQuantSetting o) {
		if (o.getId() == null || o.getId() == 0) {
			o.setId(null);
			bqQuantSettingDao.save(o);
		} else {
			bqQuantSettingDao.update(o);
		}
	}
}
