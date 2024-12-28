package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQUserTradeWhiteDao;
import com.btc100.btc_q.entity.BQUserTradeWhite;
import com.btc100.btc_q.service.BQUserTradeWhiteService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqUserTradeWhiteService")
public class BQUserTradeWhiteServiceImpl implements BQUserTradeWhiteService {

	@Autowired
	private BQUserTradeWhiteDao<BQUserTradeWhite>  bqUserTradeWhiteDao;
	
	@Override
	public PagerModel find(long uid) {
		String hql = "from BQUserTradeWhite where 1 = 1";
		Map<String, Object> map = new HashMap<String, Object>();
		if (uid > 0) {
			hql += " and f_user_id = :uid";
			map.put("uid", uid);
		}
		
		return bqUserTradeWhiteDao.searchHql(hql, map);
	}

	@Override
	public BQUserTradeWhite get(long uid) {
		return bqUserTradeWhiteDao.get(BQUserTradeWhite.class,  uid);
	}

	@Override
	public void save(BQUserTradeWhite userRiskControl) {
		bqUserTradeWhiteDao.save(userRiskControl);
	}

	@Override
	public void update(BQUserTradeWhite userRiskControl) {
		bqUserTradeWhiteDao.update(userRiskControl);
	}

	@Override
	public void delete(long uid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		bqUserTradeWhiteDao.deleteCondition("delete from BQUserTradeWhite where f_user_id = :uid", map);
		
	}

	@Override
	public List<BQUserTradeWhite> findAll() {
		return bqUserTradeWhiteDao.find("from BQUserTradeWhite where 1 = 1");
	}
	
	@Override
	public List<BQUserTradeWhite> readAll() {
		return bqUserTradeWhiteDao.find("from BQUserTradeWhite where 1 = 1");
	}

}
