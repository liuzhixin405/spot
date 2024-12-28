package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQUserAppKeyDao;
import com.btc100.btc_q.entity.BQUserAppKey;
import com.btc100.btc_q.service.BQUserAppKeyService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqUserAppKeyService")
public class BQUserAppKeyServiceImpl implements BQUserAppKeyService {

	@Autowired
	private BQUserAppKeyDao<BQUserAppKey>  bqUserAppKeyDao;
	
	@Override
	public PagerModel readPagerModel(long uid) {
		String hql = "from BQUserAppKey where 1 = 1";
		Map<String, Object> map = new HashMap<String, Object>();
		if (uid > 0) {
			hql += " and f_user_id = :uid";
			map.put("uid", uid);
		}
		
		return bqUserAppKeyDao.searchHql(hql, map);
	}

	@Override
	public BQUserAppKey readOne(long uid) {
		return bqUserAppKeyDao.get(BQUserAppKey.class,  uid);
	}

	@Override
	public void save(BQUserAppKey userAppKey) {
		bqUserAppKeyDao.save(userAppKey);
	}

	@Override
	public void update(BQUserAppKey userAppKey) {
		bqUserAppKeyDao.update(userAppKey);
	}

	@Override
	public void delete(long uid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		bqUserAppKeyDao.deleteCondition("delete from BQUserAppKey where f_user_id = :uid", map);
		
	}

	@Override
	public List<BQUserAppKey> findAll() {
		return bqUserAppKeyDao.find("from BQUserAppKey where 1 = 1");
	}

	@Override
	public BQUserAppKey getByAppKey(String appKey) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("appKey", appKey);
		return bqUserAppKeyDao.get("from BQUserAppKey where app_key = :appKey", map);
	}

	@Override
	public BQUserAppKey readByAppKey(String appKey) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("appKey", appKey);
		return bqUserAppKeyDao.get("from BQUserAppKey where app_key = :appKey", map);
	}
	
	@Override
	public PagerModel readByState(int state) {
		String hql = "from BQUserAppKey where state = :state";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("state", state);
		return bqUserAppKeyDao.searchHql(hql, map);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> readAllSubAppkey(long userId) {
		StringBuffer sql = new StringBuffer("SELECT c.f_user_id, c.f_user_email, d.app_key, d.app_secret, d.allow_ip FROM bq_user_app_key d");
		sql.append(" LEFT JOIN bq_member c ON d.f_user_id = c.f_user_id");
		sql.append(" LEFT JOIN bq_sub_member b ON b.sub_user_id = d.f_user_id");
		sql.append(" WHERE b.mother_user_id = :uid AND c.sub_user_delete <> :isDelete");
		sql.append(" ORDER BY b.id DESC");

		Session session = bqUserAppKeyDao.getSession();
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("uid", userId);
		query.setParameter("isDelete", 1);
		List<Object[]> list = query.list();
		
		return list;
	}

}
