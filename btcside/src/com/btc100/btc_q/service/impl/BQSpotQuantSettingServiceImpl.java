package com.btc100.btc_q.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQSpotQuantSettingDao;
import com.btc100.btc_q.entity.BQSpotQuantSetting;
import com.btc100.btc_q.service.BQSpotQuantSettingService;

@Service("bqSpotQuantSettingService")
public class BQSpotQuantSettingServiceImpl implements BQSpotQuantSettingService {

	@Autowired
	private BQSpotQuantSettingDao<BQSpotQuantSetting> bqQuantSettingDao;

	@Override
	public BQSpotQuantSetting get() {
		String hql = "from BQSpotQuantSetting";
		List<BQSpotQuantSetting> list = bqQuantSettingDao.find(hql);
		if (list.size() > 0) {
			return list.get(0);
		}
		
		return null;
	}
	
	@Override
	public BQSpotQuantSetting read() {
		String hql = "from BQSpotQuantSetting";
		List<BQSpotQuantSetting> list = bqQuantSettingDao.find(hql);
		if (list.size() > 0) {
			return list.get(0);
		}
		
		return null;
	}

	@Override
	public void update(BQSpotQuantSetting o) {
		if (o.getId() == null || o.getId() == 0) {
			o.setId(null);
			bqQuantSettingDao.save(o);
		} else {
			bqQuantSettingDao.update(o);
		}
	}
}
