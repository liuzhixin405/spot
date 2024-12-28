package com.btc100.btc_q.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.dao.BQAccountAmountLogDao;
import com.btc100.btc_q.entity.BQAccountAmountLog;

@Repository("bqAccountAmountLogDao")
public class BQAccountAmountLogDaoImpl extends BaseDaoImpl<BQAccountAmountLog> implements BQAccountAmountLogDao<BQAccountAmountLog> {

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findSumZRZCRecord(long userId, int currencyId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		map.put("operationzr", Contents.TRANS_TYPE_TO);
		map.put("operationzc", Contents.TRANS_TYPE_OUT);
		map.put("currencyId", currencyId);
		
		String hql="SELECT SUM(t.change_amount) AS amount, t.operation AS operation from BQAccountAmountLog t where t.f_user_id = :userId AND currency_type_id = :currencyId"
				+ " AND (t.operation = :operationzr or t.operation = :operationzc) group by t.operation";

		return queryparams(hql, map).list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findSumZRZCOTCRecord(long userId, int currencyId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		map.put("operationzrotc", Contents.TRANS_TYPE_TOOTC);
		map.put("operationzcotc", Contents.TRANS_TYPE_OUTOTC);
		map.put("currencyId", currencyId);
		
		String hql="SELECT SUM(t.change_amount) AS amount, t.operation AS operation from BQAccountAmountLog t where t.f_user_id = :userId AND currency_type_id = :currencyId"
				+ " AND (t.operation = :operationzrotc or t.operation = :operationzcotc) group by t.operation";
		
		return queryparams(hql, map).list();
	}

	@Override
	public void createAccountAmountLogBak() {
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				
				final String create_table_sql = "create table if not exists bq_account_amount_log_bak like bq_account_amount_log;";
				
				Statement ps = con.createStatement();
				ps.execute(create_table_sql);
			}
			
		});
	}

	@Override
	public void insertAccountAmountLogBak(final long endTime) {
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				
				String insert_sql = "insert into bq_account_amount_log_bak select * from bq_account_amount_log"
						+ " where operation <> " + Contents.TRANS_TYPE_TO + " and operation <> " + Contents.TRANS_TYPE_OUT + " and dateline < " + endTime;
				
				System.out.println(insert_sql);
				Statement ps = con.createStatement();
				ps.execute(insert_sql);
			}

		});
	}

	@Override
	public void deleteAccountAmountLog(long endTime) {
		Session session = getSession();
		String sql = "DELETE FROM bq_account_amount_log WHERE operation <> " + Contents.TRANS_TYPE_TO + " AND operation <> " + Contents.TRANS_TYPE_OUT + " AND dateline < ?";
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, endTime);
		query.executeUpdate();
	}

}
