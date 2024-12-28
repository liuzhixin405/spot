package com.btc100.btc_q.dao.impl;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.NTradeLogIncomeReconciliaDao;
import com.btc100.btc_q.entity.NTradeLogIncomeReconcilia;

@Repository("nTradeLogIncomeReconciliaDao")
public class NTradeLogIncomeReconciliaDaoImpl extends BaseDaoImpl<NTradeLogIncomeReconcilia> implements NTradeLogIncomeReconciliaDao<NTradeLogIncomeReconcilia> {

	@Override
	public int insert(String partition, NTradeLogIncomeReconcilia log) {
		Session session = getSession();
		String sql = "insert into " + partition + "_trade_log_income_reconcilia "
				+ "(f_user_id,income,cut_off_time) values (?,?,?)";

		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, log.getF_user_id());
		query.setBigDecimal(1, log.getIncome());
		query.setLong(2, log.getCut_off_time());
		
		return query.executeUpdate();
	}

	@Override
	public int update(String partition, NTradeLogIncomeReconcilia log) {
		Session session = getSession();
		String sql = "update " + partition + "_trade_log_income_reconcilia set "
				+ "f_user_id=?,income=?,cut_off_time=? where id=?";

		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, log.getF_user_id());
		query.setBigDecimal(1, log.getIncome());
		query.setLong(2, log.getCut_off_time());
		query.setLong(3, log.getId());
		
		return query.executeUpdate();
	}
	
}
