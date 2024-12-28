package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQBfxFeeRecordDao;

@Repository("bqBfxFeeRecordDao")
public class BQBfxFeeRecordDaoImpl<T> extends BaseDaoImpl<T> implements BQBfxFeeRecordDao<T> {

	@Override
	public BigDecimal sumAmount(long userId) {
		String sql = "select IFNULL(sum(b.amount),0) from bq_bfx_fee_record b where 1=1";
		
		if (userId > 0) {
			sql += " and b.f_user_id = :uid";
		}
		
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(sql);
		if (userId > 0) {
			query.setLong("uid", userId);
		}
		
		return (BigDecimal) query.uniqueResult();
	}

}
