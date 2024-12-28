package com.btc100.btc_q.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQUserApiSettingDao;
import com.btc100.btc_q.entity.BQUserApiSetting;
import com.btc100.btc_q.service.BQUserApiSettingService;
import com.btc100.btc_q.util.StringUtil;

@Service("bqUserApiSettingService")
public class BQUserApiSettingServiceImpl implements BQUserApiSettingService {

	@Autowired
	private BQUserApiSettingDao<BQUserApiSetting>  bqUserApiSettingDao;

	@Override
	public BQUserApiSetting readOne() {
		String hql = "from BQUserApiSetting";
		List<BQUserApiSetting> list = bqUserApiSettingDao.find(hql);
		if (list.size() > 0) {
			return list.get(0);
		}
		
		return null;
	}

	@Override
	public void update(BQUserApiSetting setting) {
		if (StringUtil.isEmpty(setting.getId()) || setting.getId() == 0) {
			bqUserApiSettingDao.save(setting);
		} else {
			bqUserApiSettingDao.update(setting);
		}
	}
	
	
}
