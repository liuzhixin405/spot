package com.btc100.btc_q.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.SpotAccountLogDao;

@Repository("spotAccountLogDao")
public class SpotAccountLogDaoImpl<T> extends BaseDaoImpl<T> implements SpotAccountLogDao<T> {
	public com.btc100.btc_q.entity.SpotAccountLog insertLog(final com.btc100.btc_q.entity.SpotAccountLog accountLog) {
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				PreparedStatement psmt = con.prepareStatement(
								"insert into "
										+ accountLog.getF_coin_pair_name()
										+ "_account_log (title, currency_type_id, operation, direction, current_amount, change_amount, lastest_amount, " +
												"dateline, f_user_id, trans_no, record_id, f_coin_pair_name, before_frozen, after_frozen)"
										+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
								Statement.RETURN_GENERATED_KEYS);
				psmt.setString(1, accountLog.getTitle());
				psmt.setInt(2, accountLog.getCurrency_type_id());
				psmt.setInt(3, accountLog.getOperation());
				psmt.setInt(4, accountLog.getDirection());
				psmt.setBigDecimal(5, accountLog.getCurrent_amount());
				psmt.setBigDecimal(6, accountLog.getChange_amount());
				psmt.setBigDecimal(7, accountLog.getLastest_amount());
				psmt.setLong(8, accountLog.getDateline());
				psmt.setLong(9, accountLog.getF_user_id());
				psmt.setString(10, accountLog.getTrans_no());
				psmt.setLong(11, accountLog.getRecord_id());
				psmt.setString(12, accountLog.getF_coin_pair_name());
				psmt.setBigDecimal(13, accountLog.getBefore_frozen());
				psmt.setBigDecimal(14, accountLog.getAfter_frozen());
				psmt.executeUpdate();
				ResultSet results = psmt.getGeneratedKeys();
				long num = -1;
				if (results.next()) {
					num = results.getLong(1);
				}

				accountLog.setLog_id(num);
			}

		});

		return accountLog;
	}

	@Override
	public void createAccountLogBak(final String code) {
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				
				final String create_table_sql = "create table if not exists " + code + "_account_log_bak like " + code + "_account_log;";
				
				Statement ps = con.createStatement();
				ps.execute(create_table_sql);
			}

		});
	}

	@Override
	public void insertAccountLogBak(final String code, final long endTime) {
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				
				String insert_sql = "insert into " + code + "_account_log_bak select * from " + code + "_account_log"
						+ " where operation <> 1 and operation <> 2 and dateline < " + endTime;
				System.out.println(insert_sql);
				Statement ps = con.createStatement();
				ps.execute(insert_sql);
			}
			
		});
	}

	@Override
	public void deleteAccountLog(String code, long endTime) {
		Session session = getSession();
		String sql = "DELETE FROM " + code + "_account_log WHERE operation <> 1 AND operation <> 2 AND dateline < ?";
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, endTime);
		query.executeUpdate();
	}
}
