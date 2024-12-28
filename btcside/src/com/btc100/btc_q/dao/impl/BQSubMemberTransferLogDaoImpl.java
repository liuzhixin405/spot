package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQSubMemberTransferLogDao;

@Repository("bqSubMemberTransferLogDao")
public class BQSubMemberTransferLogDaoImpl<T> extends BaseDaoImpl<T> implements BQSubMemberTransferLogDao<T> {

	@Override
	public BigDecimal sumAmount(long userId, int currencyId, boolean isIn) {
		StringBuffer sql = new StringBuffer("select IFNULL(sum(b.amount),0) from bq_sub_member_transfer_log b where b.currency_type_id = :currencyId");
		if (isIn) {
			sql.append(" AND ((b.mother_user_id = :uid and b.operation = 2) or (b.sub_user_id = :uid and b.operation = 1))");
		} else {
			sql.append(" AND ((b.mother_user_id = :uid and b.operation = 1) or (b.sub_user_id = :uid and b.operation = 2))");
		}
		
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setInteger("currencyId", currencyId);
		query.setLong("uid", userId);
		return (BigDecimal) query.uniqueResult();
	}

}
