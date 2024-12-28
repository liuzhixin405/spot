package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQUserMonitorDao;
import com.btc100.btc_q.entity.BQUserMonitor;
import com.btc100.btc_q.service.BQUserMonitorService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqUserMonitorService")
public class BQUserMonitorServiceImpl implements BQUserMonitorService {

	@Autowired
	private BQUserMonitorDao<BQUserMonitor>  bqUserMonitorDao;
	
	@Override
	public PagerModel find(long uid) {
		String hql = "from BQUserMonitor where 1 = 1";
		Map<String, Object> map = new HashMap<String, Object>();
		if (uid > 0) {
			hql += " and f_user_id = :uid";
			map.put("uid", uid);
		}
		
		return bqUserMonitorDao.searchHql(hql, map);
	}

	@Override
	public BQUserMonitor get(long uid) {
		return bqUserMonitorDao.get(BQUserMonitor.class,  uid);
	}

	@Override
	public void save(BQUserMonitor userMonitor) {
		bqUserMonitorDao.save(userMonitor);
	}

	@Override
	public void update(BQUserMonitor userMonitor) {
		bqUserMonitorDao.update(userMonitor);
	}

	@Override
	public void delete(long uid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		bqUserMonitorDao.deleteCondition("delete from BQUserMonitor where f_user_id = :uid", map);
		
	}

	@Override
	public List<BQUserMonitor> findAll() {
		return bqUserMonitorDao.find("from BQUserMonitor where 1 = 1");
	}

}
