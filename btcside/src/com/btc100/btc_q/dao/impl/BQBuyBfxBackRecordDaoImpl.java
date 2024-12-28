package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQBuyBfxBackRecordDao;

@Repository("bqBuyBfxBackRecordDao")
public class BQBuyBfxBackRecordDaoImpl<T> extends BaseDaoImpl<T> implements BQBuyBfxBackRecordDao<T> {


	@Override
	public BigDecimal sumBfxAmount(long uid) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("SELECT ifnull(SUM(amount), 0) from bq_buy_bfx_back_record where f_user_id = :uid ");
		sql.append(" and state = :state");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("uid", uid);
		query.setParameter("state", 1);
		return (BigDecimal)query.uniqueResult();
	}

	@Override
	public BigDecimal sumObtain(long uid, int currencyId) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("SELECT ifnull(SUM(money), 0) from bq_buy_bfx_back_record where f_user_id = :uid ");
		sql.append(" and state = :state");
		sql.append(" and currency_type_id = :currencyId ");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("uid", uid);
		query.setParameter("state", 1);
		query.setParameter("currencyId", currencyId);
		return (BigDecimal)query.uniqueResult();
	}

	@Override
	public BigDecimal sumBfxAmountFrozen(long uid) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("SELECT ifnull(SUM(amount), 0) from bq_buy_bfx_back_record where f_user_id = :uid ");
		sql.append(" and state = :state");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("uid", uid);
		query.setParameter("state", 0);
		return (BigDecimal)query.uniqueResult();
	}
}
