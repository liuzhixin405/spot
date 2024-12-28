package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQExchangeBfcRecordDao;

@Repository("bqExchangeBfcRecordDao")
public class BQExchangeBfcRecordDaoImpl<T> extends BaseDaoImpl<T> implements BQExchangeBfcRecordDao<T> {

	@Override
	public BigDecimal sumFrozen(long uid, int currencyId) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer(
				"SELECT ifnull(SUM(ori_amount), 0) from bq_exchange_bfc_record where f_user_id = :uid and (");
		sql.append("state = :state0");
		sql.append(" or state = :state1");
		sql.append(" or state = :state2)");
		sql.append(" and ori_currency_type_id = :currencyId ");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("uid", uid);
		query.setParameter("state0", 0);
		query.setParameter("state1", 1);
		query.setParameter("state2", 2);
		query.setParameter("currencyId", currencyId);
		return (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal sumDeduction(long uid, int currencyId) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer(
				"SELECT ifnull(SUM(ori_amount), 0) from bq_exchange_bfc_record where f_user_id = :uid and (");
		sql.append("state = :state0");
		sql.append(" or state = :state1)");
		sql.append(" and ori_currency_type_id = :currencyId ");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("uid", uid);
		query.setParameter("state0", 3);
		query.setParameter("state1", 4);
		query.setParameter("currencyId", currencyId);
		return (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal sumObtain(long uid, int currencyId) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer(
				"SELECT ifnull(SUM(target_amount), 0) from bq_exchange_bfc_record where f_user_id = :uid ");
		sql.append(" and state = :state");
		sql.append(" and target_currency_type_id = :currencyId ");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("uid", uid);
		query.setParameter("state", 4);
		query.setParameter("currencyId", currencyId);
		return (BigDecimal) query.uniqueResult();
	}

}
