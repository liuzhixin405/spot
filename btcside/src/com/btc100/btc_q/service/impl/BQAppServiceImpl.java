package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQAppDao;
import com.btc100.btc_q.entity.BQApp;
import com.btc100.btc_q.service.BQAppService;

@Service("bqAppService")
public class BQAppServiceImpl implements BQAppService {

	@Autowired
	private BQAppDao<BQApp> bqAppDao;

	@Override
	public BQApp readOne() {
		String hql = "from BQApp";
		List<BQApp> list = bqAppDao.find(hql);
		if (list.size() > 0) {
			return list.get(0);
		}
		
		return null;
	}

	@Override
	public void update(BQApp o) {
		o.setUpdate_time(System.currentTimeMillis() / 1000);
		if (o.getId() == null || o.getId() == 0) {
			o.setId(null);
			bqAppDao.save(o);
		} else {
			bqAppDao.update(o);
		}
	}

	@Override
	public void addIOSTimes() {
		String hql = "UPDATE BQApp b SET b.download_times=b.download_times + 1 WHERE b.platform = 'Android'";
		Map<String, Object> map = new HashMap<String, Object>();
		bqAppDao.updateCondition(hql, map);
		
	}

	@Override
	public void addAndroidTimes() {
		String hql = "UPDATE BQApp b SET b.download_times=b.download_times + 1 WHERE b.platform = 'IOS'";
		Map<String, Object> map = new HashMap<String, Object>();
		bqAppDao.updateCondition(hql, map);
		
	}

	@Override
	public BQApp readByPlatform(String platform) {
		String hql = "from BQApp where platform = :platform";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("platform", platform);

		return bqAppDao.get(hql, params);
	}
	
	@Override
	public List<BQApp> readAll() {
		String hql = "from BQApp";
		List<BQApp> list = bqAppDao.find(hql);
		return list;
	}
}
