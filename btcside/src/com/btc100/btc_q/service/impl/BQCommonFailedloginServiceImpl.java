package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQCommonFailedloginDao;
import com.btc100.btc_q.entity.BQCommonFailedlogin;
import com.btc100.btc_q.service.BQCommonFailedloginService;
@Service("bqCommonFailedloginService")
public class BQCommonFailedloginServiceImpl implements BQCommonFailedloginService{
	@Autowired
	private BQCommonFailedloginDao<BQCommonFailedlogin> bqCommonFailedloginDao;
	
	public void save(BQCommonFailedlogin bqFl) {
		bqCommonFailedloginDao.save(bqFl);
		
	}

	public void clearLoginTime(String email) {
		final String hql="update BQCommonFailedlogin u set u.count=0,u.google_count=0 where u.username=:name";
		Map<String, Object>  params = new HashMap<String, Object> ();
		params.put("name", email);
		bqCommonFailedloginDao.updateCondition(hql, params);
		
	}

	public void updateLoginTime(BQCommonFailedlogin bqFl) {
		bqCommonFailedloginDao.update(bqFl);	
	}

	public BQCommonFailedlogin readByName(String email) {
		Session session=bqCommonFailedloginDao.getSession();
		String sql = "select * from bq_common_failedlogin where email=?";
		SQLQuery query = session.createSQLQuery(sql);
		query.setString(0,email);
		query.addEntity(BQCommonFailedlogin.class);
		return (BQCommonFailedlogin) query.uniqueResult();		
	}

	public void clearGoogleLoginTime(String email) {
		final String hql="update BQCommonFailedlogin u set u.google_count=0 where u.username=:name";
		Map<String, Object>  params = new HashMap<String, Object> ();
		params.put("name", email);
		bqCommonFailedloginDao.updateCondition(hql, params);
	}

}
