package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQExchangeRecordDao;

@Repository("bqExchangeRecordDao")
public class BQExchangeRecordDaoImpl<T> extends BaseDaoImpl<T> implements BQExchangeRecordDao<T> {
	@Override
	public BigDecimal sumFrozen(long uid, int currencyId) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("SELECT ifnull(SUM(frozen_amount), 0) from bq_exchange_record where f_user_id = :uid and (");
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
		return (BigDecimal)query.uniqueResult();
	}

	@Override
	public BigDecimal sumDeduction(long uid, int currencyId) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("SELECT ifnull(SUM(frozen_amount), 0) from bq_exchange_record where f_user_id = :uid ");
		sql.append(" and state = :state");
		sql.append(" and ori_currency_type_id = :currencyId ");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("uid", uid);
		query.setParameter("state", 3);
		query.setParameter("currencyId", currencyId);
		BigDecimal sum1 = (BigDecimal)query.uniqueResult();
		
		sql = new StringBuffer("SELECT ifnull(SUM(frozen_amount - frozen_amount_back), 0) from bq_exchange_record where f_user_id = :uid ");
		sql.append(" and state = :state");
		sql.append(" and ori_currency_type_id = :currencyId ");
		query = session.createSQLQuery(sql.toString());
		query.setParameter("uid", uid);
		query.setParameter("state", 4);
		query.setParameter("currencyId", currencyId);
		BigDecimal sum2 = (BigDecimal)query.uniqueResult();
		
		return sum1.add(sum2);
	}

	@Override
	public BigDecimal sumObtain(long uid, int currencyId) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("SELECT ifnull(SUM(obtain_amount), 0) from bq_exchange_record where f_user_id = :uid ");
		sql.append(" and state = :state");
		sql.append(" and target_currency_type_id = :currencyId ");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("uid", uid);
		query.setParameter("state", 4);
		query.setParameter("currencyId", currencyId);
		return (BigDecimal)query.uniqueResult();
	}
}
