package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQMemberSafeSettingsDao;
import com.btc100.btc_q.entity.BQMemberSafeSettings;
import com.btc100.btc_q.service.BQMemberSafeSettingsService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqMemberSafeSettingsService")
public class BQMemberSafeSettingsServiceImpl implements BQMemberSafeSettingsService {

	@Autowired
	private BQMemberSafeSettingsDao<BQMemberSafeSettings> bqMemberSafeSettingsDao;

	public void save(BQMemberSafeSettings bqMem) {
		bqMemberSafeSettingsDao.save(bqMem);
	}

	public BQMemberSafeSettings readById(long id) {
		Session session = bqMemberSafeSettingsDao.getSession();
		final String sql = "select * from  bq_member_safe_settings where f_user_id= ?";
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, id);
		query.addEntity(BQMemberSafeSettings.class);
		return (BQMemberSafeSettings) query.uniqueResult();
	}
	
	public BQMemberSafeSettings getById(long id) {
		Session session = bqMemberSafeSettingsDao.getSession();
		final String sql = "select * from  bq_member_safe_settings where f_user_id= ?";
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, id);
		query.addEntity(BQMemberSafeSettings.class);
		return (BQMemberSafeSettings) query.uniqueResult();
	}
	
	public BQMemberSafeSettings readByTel(String tel) {
		Session session = bqMemberSafeSettingsDao.getSession();
		final String sql = "select * from  bq_member_safe_settings where f_user_tel= ?";
		SQLQuery query = session.createSQLQuery(sql);
		query.setFirstResult(0);
		query.setMaxResults(1);
		query.setString(0, tel);
		query.addEntity(BQMemberSafeSettings.class);
		return (BQMemberSafeSettings) query.uniqueResult();
	}

	public void updateTel(BQMemberSafeSettings bqms) {
		bqMemberSafeSettingsDao.update(bqms);
	}

	public void updateSafe(BQMemberSafeSettings bqs) {
		bqMemberSafeSettingsDao.update(bqs);
	}

	public boolean checkUserSafe(long uid) {
		BQMemberSafeSettings settings = bqMemberSafeSettingsDao.get(BQMemberSafeSettings.class, uid);
		if (settings.getF_user_safetype() == 0) {
			return true;
		}
		return false;
	}

	public PagerModel readUserSafe(long userId) {
		if (userId <= 0) {
			return bqMemberSafeSettingsDao.searchHql("From BQMemberSafeSettings ORDER BY f_user_safetype DESC");
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userID", userId);
		return bqMemberSafeSettingsDao.searchHql("From BQMemberSafeSettings WHERE f_user_id = :userID ORDER BY f_user_safetype DESC", params);
	}

	public void removeGoogle(long userId) {
		BQMemberSafeSettings safeSettings = bqMemberSafeSettingsDao.get(BQMemberSafeSettings.class, userId);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userID", userId);
		if ("".equals(safeSettings.getF_user_tel())) 
			bqMemberSafeSettingsDao.updateCondition("UPDATE BQMemberSafeSettings SET f_user_safetype = 0, f_user_safegoogle = '' WHERE f_user_id = :userID AND f_user_safetype = 2 ", params);			
		 else 
			bqMemberSafeSettingsDao.updateCondition("UPDATE BQMemberSafeSettings SET f_user_safetype = 1, f_user_safegoogle = '' WHERE f_user_id = :userID AND f_user_safetype = 3 ", params);
	}

	@Override
	public boolean readHaveTel(String tel) {
		Session session = bqMemberSafeSettingsDao.getSession();
		final String sql = "select * from  bq_member_safe_settings where f_user_tel= ?";
		SQLQuery query = session.createSQLQuery(sql);
		query.setString(0, tel);
		query.addEntity(BQMemberSafeSettings.class);
		return query.uniqueResult() != null;
	}

}
