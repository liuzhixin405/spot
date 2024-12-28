package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQUserRiskControlDao;
import com.btc100.btc_q.entity.BQUserRiskControl;
import com.btc100.btc_q.service.BQUserRiskControlService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqUserRiskControlService")
public class BQUserRiskControlServiceImpl implements BQUserRiskControlService {

	@Autowired
	private BQUserRiskControlDao<BQUserRiskControl>  bqUserRiskControlDao;
	
	@Override
	public PagerModel find(long uid) {
		String hql = "from BQUserRiskControl where 1 = 1";
		Map<String, Object> map = new HashMap<String, Object>();
		if (uid > 0) {
			hql += " and f_user_id = :uid";
			map.put("uid", uid);
		}
		
		return bqUserRiskControlDao.searchHql(hql, map);
	}

	@Override
	public BQUserRiskControl readOne(long uid) {
		return bqUserRiskControlDao.get(BQUserRiskControl.class,  uid);
	}

	@Override
	public void save(BQUserRiskControl userRiskControl) {
		bqUserRiskControlDao.save(userRiskControl);
	}

	@Override
	public void update(BQUserRiskControl userRiskControl) {
		bqUserRiskControlDao.update(userRiskControl);
	}

	@Override
	public void delete(long uid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		bqUserRiskControlDao.deleteCondition("delete from BQUserRiskControl where f_user_id = :uid", map);
		
	}

	@Override
	public List<BQUserRiskControl> findAll() {
		return bqUserRiskControlDao.find("from BQUserRiskControl where 1 = 1");
	}

}
