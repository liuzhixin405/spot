package com.btc100.btc_q.dao.impl;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQCommonCreditLogDao;
import com.btc100.btc_q.entity.BQCommonCreditLog;

@Repository("bqCommonCreditLogDao")
public class BQCommonCreditLogDaoImpl<T> extends BaseDaoImpl<T> implements BQCommonCreditLogDao<T>{

	@Override
	public BQCommonCreditLog getByUserIdAndType(long userId, int type) {
		
		Session session = getSession();
		String sql = "select * FROM bq_common_credit_log WHERE f_user_id = ? and credit_type_id = ? limit 0,1 ";

		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity(BQCommonCreditLog.class);
		query.setLong(0, userId);
		query.setInteger(1, type);
		
		return (BQCommonCreditLog) query.uniqueResult();
	}
}
