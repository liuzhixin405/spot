package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQIndexDescriptionDao;
import com.btc100.btc_q.entity.BQIndexDescription;
import com.btc100.btc_q.service.BQIndexDescriptionService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqIndexDescriptionService")
public class BQIndexDescriptionServiceImpl implements BQIndexDescriptionService {

	@Autowired
	private BQIndexDescriptionDao<BQIndexDescription> bqIndexDescriptionDao;

	@Override
	public int add(BQIndexDescription obj) {
		bqIndexDescriptionDao.save(obj);
		return 1;
	}

	@Override
	public int update(BQIndexDescription obj) {
		bqIndexDescriptionDao.update(obj);
		return 1;
	}

	@Override
	public int delById(int id) {
		String hql = "DELETE BQIndexDescription b where b.id=:id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);

		return bqIndexDescriptionDao.deleteCondition(hql, map);
	}

	@Override
	public BQIndexDescription readById(int id) {
		String hql = "FROM BQIndexDescription b where b.id=:id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);

		return bqIndexDescriptionDao.get(hql, map);
	}
	
	@Override
	public BQIndexDescription readByCode(String code) {
		String hql = "FROM BQIndexDescription b where b.type_code=:code";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", code);

		return bqIndexDescriptionDao.get(hql, map);
	}

	@Override
	public PagerModel readPagerModel() {
		String hql = "FROM BQIndexDescription";
		return bqIndexDescriptionDao.searchHql(hql);
	}

}
