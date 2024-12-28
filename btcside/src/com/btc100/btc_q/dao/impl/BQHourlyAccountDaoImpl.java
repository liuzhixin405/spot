package com.btc100.btc_q.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQHourlyAccountDao;

@Repository("bqHourlyAccountDao")
public class BQHourlyAccountDaoImpl<T> extends BaseDaoImpl<T> implements BQHourlyAccountDao<T> {

	@Override
	public void createHourlyAccountBak() {
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				
				final String create_table_sql = "create table if not exists bq_hourly_account_bak like bq_hourly_account;";
				
				Statement ps = con.createStatement();
				ps.execute(create_table_sql);
			}
			
		});
	}

	@Override
	public void insertHourlyAccountBak(final long endTime) {
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				
				String insert_sql = "insert into bq_hourly_account_bak select * from bq_hourly_account"
						+ " where hour_time < " + endTime;
				
				System.out.println(insert_sql);
				Statement ps = con.createStatement();
				ps.execute(insert_sql);
			}

		});
	}

	@Override
	public void deleteHourlyAccount(long endTime) {
		Session session = getSession();
		String sql = "DELETE FROM bq_hourly_account WHERE hour_time < ?";
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, endTime);
		query.executeUpdate();
	}
	
}
