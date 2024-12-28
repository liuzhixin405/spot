package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.NTradeLogReconciliaDao;
import com.btc100.btc_q.entity.NTradeLogReconcilia;

@Repository("nTradeLogReconciliaDao")
public class NTradeLogReconciliaDaoImpl extends BaseDaoImpl<NTradeLogReconcilia> implements NTradeLogReconciliaDao<NTradeLogReconcilia> {

	@Autowired
	SpyMemcachedClient spyMemcachedClient;

	@Override
	public void insert(String partition, NTradeLogReconcilia form) {
		Session session = getSession();
		String sql = "insert into " + partition + "_trade_log_reconcilia "
				+ "(transaction_id,transaction_code,trade_income,trade_fee,f_user_id,time) values (?,?,?,?,?,?)";

		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, form.getTransaction_id());
		query.setString(1, form.getTransaction_code());
		query.setBigDecimal(2, form.getTrade_income());
		query.setBigDecimal(3, form.getTrade_fee());
		query.setLong(4, form.getF_user_id());
		query.setLong(5, form.getTime());
		
		query.executeUpdate();
	}

	@Override
	public NTradeLogReconcilia getByTimeAndUser(String partition, String code, long time, long userId) {
		String sql = "SELECT * FROM " + partition + "_trade_log_reconcilia WHERE time = :time AND transaction_code = :code AND f_user_id = :userId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("time", time);
		params.put("code", code);
		params.put("userId", userId);
		
		return (NTradeLogReconcilia) getBySql(sql, params);
	}

	@Override
	public Object[] getIncomeAndFeeByTimeAndUser(String partition, String code, long userId, long startTime, long endTime) {
		String sql = "SELECT ifnull(SUM(trade_income), 0) AS amount, ifnull(SUM(trade_fee), 0) AS fee FROM " + partition + "_trade_log_reconcilia "
				+ "WHERE transaction_code = :code and f_user_id = :userId and time >= :startTime and time < :endTime";
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("code", code);
		query.setParameter("userId", userId);
		query.setParameter("startTime", startTime);
		query.setParameter("endTime", endTime);
		
		return (Object[]) query.uniqueResult();
	}

	@Override
	public NTradeLogReconcilia getByUser(String partition, String code, long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("userId", userId);
		return (NTradeLogReconcilia) getBySql("select * from " + partition + "_trade_log_reconcilia t where t.transaction_code = :code and t.f_user_id = :userId", params);
	}

	@Override
	public void update(String partition, NTradeLogReconcilia form) {
		Session session = getSession();
		String sql = "update " + partition + "_trade_log_reconcilia set "
				+ "transaction_id=?,transaction_code=?,trade_income=?,trade_fee=?,f_user_id=?,time=? where id=?";

		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, form.getTransaction_id());
		query.setString(1, form.getTransaction_code());
		query.setBigDecimal(2, form.getTrade_income());
		query.setBigDecimal(3, form.getTrade_fee());
		query.setLong(4, form.getF_user_id());
		query.setLong(5, form.getTime());
		query.setLong(6, form.getId());
		
		query.executeUpdate();
	}

}
