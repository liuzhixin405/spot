package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.TradeLogReconciliaDao;
import com.btc100.btc_q.entity.TradeLogReconcilia;

@Repository("tradeLogReconciliaDao")
public class TradeLogReconciliaDaoImpl<T> extends BaseDaoImpl<T> implements TradeLogReconciliaDao<T> {

	@Autowired
	SpyMemcachedClient spyMemcachedClient;

	@Override
	public void insert(TradeLogReconcilia e) {
		Session session = getSession();
		session.save(e);
	}

	@Override
	public TradeLogReconcilia getByTimeAndUser(String code, long time, long userId) {
		String sql = "SELECT * FROM trade_log_reconcilia WHERE time = :time AND transaction_code = :code AND f_user_id = :userId";
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(sql.toString()).addEntity(TradeLogReconcilia.class);
		query.setParameter("time", time);
		query.setParameter("code", code);
		query.setParameter("userId", userId);
		
		@SuppressWarnings("unchecked")
		List<TradeLogReconcilia> list = query.list();
		if (list.size() > 0) {
			return list.get(0);
		}
		
		return null;
	}

	@Override
	public Object[] getIncomeAndFeeByTimeAndUser(String code, long userId, long startTime, long endTime) {
		String sql = "SELECT ifnull(SUM(trade_income), 0) AS amount, ifnull(SUM(trade_fee), 0) AS fee FROM trade_log_reconcilia "
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
	public TradeLogReconcilia getByUser(String code, long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("userId", userId);
		return (TradeLogReconcilia) get(" from TradeLogReconcilia t where t.transaction_code = :code and t.f_user_id = :userId", params);
	}

}
