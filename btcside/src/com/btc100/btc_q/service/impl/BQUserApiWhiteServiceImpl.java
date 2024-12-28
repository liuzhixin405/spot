package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQUserApiWhiteDao;
import com.btc100.btc_q.entity.BQUserApiWhite;
import com.btc100.btc_q.service.BQUserApiWhiteService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqUserApiWhiteService")
public class BQUserApiWhiteServiceImpl implements BQUserApiWhiteService {

	@Autowired
	private BQUserApiWhiteDao<BQUserApiWhite> bqUserTradeWhiteDao;

	@Override
	public PagerModel find(long uid) {
		String hql = "from BQUserApiWhite where 1 = 1";
		Map<String, Object> map = new HashMap<String, Object>();
		if (uid > 0) {
			hql += " and f_user_id = :uid";
			map.put("uid", uid);
		}

		return bqUserTradeWhiteDao.searchHql(hql, map);
	}

	@Override
	public BQUserApiWhite get(long uid) {
		return bqUserTradeWhiteDao.get(BQUserApiWhite.class, uid);
	}

	@Override
	public void save(BQUserApiWhite obj) {
		bqUserTradeWhiteDao.save(obj);
	}

	@Override
	public void update(BQUserApiWhite obj) {
		bqUserTradeWhiteDao.update(obj);
	}

	@Override
	public void delete(long uid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		bqUserTradeWhiteDao.deleteCondition("delete from BQUserApiWhite where f_user_id = :uid", map);
	}

	@Override
	public List<BQUserApiWhite> readAll() {
		return bqUserTradeWhiteDao.find("from BQUserApiWhite where 1 = 1");
	}

}
