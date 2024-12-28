package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQDataBroadCastSettingDao;
import com.btc100.btc_q.entity.BQDataBroadCastSetting;
import com.btc100.btc_q.service.BQDataBroadCastSettingService;

@Service("bqDataBroadCastSettingService")
public class BQDataBroadCastSettingServiceImpl implements BQDataBroadCastSettingService {

	@Autowired
	private BQDataBroadCastSettingDao<BQDataBroadCastSetting> bqDataBroadCastSettingDao;

	@Override
	public BQDataBroadCastSetting getByCode(String code) {
		String hql = "from BQDataBroadCastSetting where f_code = :code";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return bqDataBroadCastSettingDao.get(hql, params);
	}

	@Override
	public void save(BQDataBroadCastSetting setting) {
		bqDataBroadCastSettingDao.save(setting);
	}

	@Override
	public List<BQDataBroadCastSetting> readAll() {
		return bqDataBroadCastSettingDao.find("from BQDataBroadCastSetting where 1 = 1");
	}

	@Override
	public void update(BQDataBroadCastSetting setting) {
		bqDataBroadCastSettingDao.update(setting);
	}

}
