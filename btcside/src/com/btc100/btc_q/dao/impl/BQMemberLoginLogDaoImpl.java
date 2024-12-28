package com.btc100.btc_q.dao.impl;

import java.math.BigInteger;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQMemberLoginLogDao;

@Repository("bqMemberLoginLogDao")
public class BQMemberLoginLogDaoImpl<BQMemberLoginLog> extends BaseDaoImpl<BQMemberLoginLog>
		implements BQMemberLoginLogDao<BQMemberLoginLog> {

	@Override
	public Long getTotalLoginUserByTime(Long startTime, Long endTime) {
		Session session = getSession();
		String sql = "SELECT COUNT(*) FROM (SELECT DISTINCT f_user_id FROM bq_member_login_log "
				+ "where login_time > " + startTime + " and login_time <= " + endTime + ") T ";
		SQLQuery query = session.createSQLQuery(sql);
		BigInteger total = (BigInteger) query.uniqueResult();
		return total.longValue();
	}
	
}
