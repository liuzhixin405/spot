package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQUserOpenLimitDao;
import com.btc100.btc_q.entity.BQUserOpenLimit;
import com.btc100.btc_q.service.BQUserOpenLimitService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqUserOpenLimitService")
public class BQUserOpenLimitServiceImpl implements BQUserOpenLimitService {

	@Autowired
	private BQUserOpenLimitDao<BQUserOpenLimit>  bqUserOpenLimitDao;
	
	@Override
	public BQUserOpenLimit get(String code, long uid) {
		String hql = "from BQUserOpenLimit where f_user_id = :uid and f_code = :code";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		map.put("code", code);
		
		return bqUserOpenLimitDao.get(hql, map);
	}

	@Override
	public void save(BQUserOpenLimit obj) {
		bqUserOpenLimitDao.save(obj);
	}

	@Override
	public void update(BQUserOpenLimit obj) {
		bqUserOpenLimitDao.update(obj);
	}

	@Override
	public void delete(BQUserOpenLimit obj) {
		bqUserOpenLimitDao.delete(obj);
	}

	@Override
	public PagerModel read(String code, long uid) {
		String hql = "from BQUserOpenLimit where 1 = 1";
		Map<String, Object> map = new HashMap<String, Object>();
		if (uid > 0) {
			hql += " and f_user_id = :uid";
			map.put("uid", uid);
		}
		if (!StringUtil.isEmpty(code)) {
			hql += " and f_code = :code";
			map.put("code", code);
		}
		
		return bqUserOpenLimitDao.searchHql(hql, map);
	}

	@Override
	public BQUserOpenLimit readById(long id) {
		return bqUserOpenLimitDao.get(BQUserOpenLimit.class, id);
	}

	@Override
	public BQUserOpenLimit readByCodeAndUid(String code, long uid) {
		String hql = "from BQUserOpenLimit where f_user_id = :uid and f_code = :code";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		map.put("code", code);
		
		return bqUserOpenLimitDao.get(hql, map);
	}

	@Override
	public List<BQUserOpenLimit> readAll() {
		return bqUserOpenLimitDao.find("from BQUserOpenLimit");
	}

}
