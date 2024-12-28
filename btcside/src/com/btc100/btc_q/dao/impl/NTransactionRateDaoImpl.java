package com.btc100.btc_q.dao.impl;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;


import com.btc100.btc_q.dao.NTransactionRateDao;
import com.btc100.btc_q.entity.NTransactionRate;

@Repository("nTransactionRateDao")
public class NTransactionRateDaoImpl extends BaseDaoImpl<NTransactionRate> implements NTransactionRateDao<NTransactionRate> {

	@Override
	public int insert(String partition, NTransactionRate form) {
		Session session = getSession();
		String sql = "insert into " + partition + "_transaction_rate "
				+ "(transaction_code,uprate,downrate) values (?,?,?)";

		SQLQuery query = session.createSQLQuery(sql);

		query.setString(0, form.getTransaction_code());
		query.setBigDecimal(1, form.getUprate());
		query.setBigDecimal(2, form.getDownrate());
		
		return query.executeUpdate();
	}

	@Override
	public int update(String partition, NTransactionRate form) {
		Session session = getSession();
		String sql = "update " + partition + "_transaction_rate set "
				+ "transaction_code=?,uprate=?,downrate=? where id=?";

		SQLQuery query = session.createSQLQuery(sql);

		query.setString(0, form.getTransaction_code());
		query.setBigDecimal(1, form.getUprate());
		query.setBigDecimal(2, form.getDownrate());
		query.setLong(3, form.getId());
		
		return query.executeUpdate();
	} 
	
}
