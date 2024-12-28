package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.DeliveryUserOpenLimitDao;
import com.btc100.btc_q.entity.DeliveryUserOpenLimit;
import com.btc100.btc_q.service.DeliveryUserOpenLimitService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("deliveryUserOpenLimitService")
public class DeliveryUserOpenLimitServiceImpl implements DeliveryUserOpenLimitService {

	@Autowired
	private DeliveryUserOpenLimitDao<DeliveryUserOpenLimit>  bqUserOpenLimitDao;
	
	@Override
	public DeliveryUserOpenLimit get(String code, long uid) {
		String hql = "from DeliveryUserOpenLimit where f_user_id = :uid and f_code = :code";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		map.put("code", code);
		
		return bqUserOpenLimitDao.get(hql, map);
	}

	@Override
	public void save(DeliveryUserOpenLimit obj) {
		bqUserOpenLimitDao.save(obj);
	}

	@Override
	public void update(DeliveryUserOpenLimit obj) {
		bqUserOpenLimitDao.update(obj);
	}

	@Override
	public void delete(DeliveryUserOpenLimit obj) {
		bqUserOpenLimitDao.delete(obj);
	}

	@Override
	public PagerModel read(String code, long uid) {
		String hql = "from DeliveryUserOpenLimit where 1 = 1";
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
	public DeliveryUserOpenLimit readById(long id) {
		return bqUserOpenLimitDao.get(DeliveryUserOpenLimit.class, id);
	}

	@Override
	public DeliveryUserOpenLimit readByCodeAndUid(String code, long uid) {
		String hql = "from DeliveryUserOpenLimit where f_user_id = :uid and f_code = :code";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		map.put("code", code);
		
		return bqUserOpenLimitDao.get(hql, map);
	}

	@Override
	public List<DeliveryUserOpenLimit> readAll() {
		return bqUserOpenLimitDao.find("from DeliveryUserOpenLimit");
	}

}
