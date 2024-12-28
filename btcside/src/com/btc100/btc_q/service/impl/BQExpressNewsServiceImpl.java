package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQExpressNewsDao;
import com.btc100.btc_q.entity.BQExpressNews;
import com.btc100.btc_q.service.BQExpressNewsService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqExpressNewsService")
public class BQExpressNewsServiceImpl implements BQExpressNewsService {

	@Autowired
	private BQExpressNewsDao<BQExpressNews> bqExpressNewsDao;

	@Override
	public int add(BQExpressNews obj) {
		bqExpressNewsDao.save(obj);
		return 1;
	}

	@Override
	public int update(BQExpressNews obj) {
		bqExpressNewsDao.update(obj);
		return 1;
	}

	@Override
	public int delById(long id) {
		String hql = "DELETE BQExpressNews b where b.id=:id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);

		return bqExpressNewsDao.deleteCondition(hql, map);
	}

	@Override
	public BQExpressNews readById(long id) {
		String hql = "FROM BQExpressNews b where b.id=:id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);

		return bqExpressNewsDao.get(hql, map);
	}

	@Override
	public List<BQExpressNews> readLimit(int limit) {
		String hql = "FROM BQExpressNews ORDER BY dateline DESC ";

		return bqExpressNewsDao.find(hql, 1, limit);
	}

	@Override
	public PagerModel readModel() {
		String hql = "FROM BQExpressNews ORDER BY dateline DESC ";
		return bqExpressNewsDao.searchHql(hql);
	}

	@Override
	public List<BQExpressNews> readByTime(long from, String direct, long startTime, long endTime, int limit) {
		String hql = "from BQExpressNews where 1 = 1 ";
		Map<String, Object> map = new HashMap<String, Object>();
		
		if (startTime > 0) {
			hql += " and dateline >= :startTime";
			map.put("startTime", startTime);
		}
		if (endTime > 0) {
			hql += " and dateline < :endTime";
			map.put("endTime", endTime);
		}
		if (from >= 0 && "prev".equals(direct)) {
			map.put("from", from);
			hql += " and id >= :from";
		} else if (from >= 0 && "next".equals(direct)) {
			map.put("from", from);
			hql += " and id < :from";
		}
		
		hql += " order by id desc";
		
		return bqExpressNewsDao.find(hql, map, 1, limit);
	}

}
