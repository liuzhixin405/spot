package com.btc100.btc_q.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.dao.NContractAccountLogDao;
import com.btc100.btc_q.entity.NContractAccountLog;

@Repository("nContractAccountLogDao")
public class NContractAccountLogDaoImpl extends BaseDaoImpl<NContractAccountLog> implements NContractAccountLogDao<NContractAccountLog> {
	
	public NContractAccountLog insertLog(final String partition, final NContractAccountLog contractLog) {
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				PreparedStatement psmt = con.prepareStatement(
								"insert into "
										+ partition + "_" + contractLog.getF_code()
										+ "_account_log (title, currency_type_id, operation, direction, current_amount, change_amount, lastest_amount, " +
												"dateline, f_user_id, trans_no, record_id, f_code, before_frozen, after_frozen)"
										+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
								Statement.RETURN_GENERATED_KEYS);
				psmt.setString(1, contractLog.getTitle());
				psmt.setInt(2, contractLog.getCurrency_type_id());
				psmt.setInt(3, contractLog.getOperation());
				psmt.setInt(4, contractLog.getDirection());
				psmt.setBigDecimal(5, contractLog.getCurrent_amount());
				psmt.setBigDecimal(6, contractLog.getChange_amount());
				psmt.setBigDecimal(7, contractLog.getLastest_amount());
				psmt.setLong(8, contractLog.getDateline());
				psmt.setLong(9, contractLog.getF_user_id());
				psmt.setString(10, contractLog.getTrans_no());
				psmt.setLong(11, contractLog.getRecord_id());
				psmt.setString(12, contractLog.getF_code());
				psmt.setBigDecimal(13, contractLog.getBefore_frozen());
				psmt.setBigDecimal(14, contractLog.getAfter_frozen());
				psmt.executeUpdate();
				ResultSet results = psmt.getGeneratedKeys();
				long num = -1;
				if (results.next()) {
					num = results.getLong(1);
				}

				contractLog.setLog_id(num);
			}
		});

		return contractLog;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findSumZRZCRecord(String partition, String vCode, long userId) {
		String sql="SELECT ifnull(SUM(t.change_amount),0),t.operation from " + partition + "_" + vCode + "_account_log t where t.f_user_id = :userId"
				+ " AND (t.operation = :operationzr or t.operation = :operationzc) group by t.operation";
		
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("operationzr", Contents.TRANS_TYPE_CZ);
		query.setParameter("operationzc", Contents.TRANS_TYPE_TX);
		
		return query.list();
	}

	@Override
	public void createAccountLogBak(final String partition, final String code) {
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				
				final String create_table_sql = "create table if not exists " + partition + "_" + code + "_account_log_bak like " + partition + "_" + code + "_account_log;";
				
				Statement ps = con.createStatement();
				ps.execute(create_table_sql);
			}

		});
	}

	@Override
	public void insertAccountLogBak(final String partition, final String code, final long endTime) {
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				
				String insert_sql = "insert into " + partition + "_" + code + "_account_log_bak select * from " + partition + "_" + code + "_account_log"
						+ " where operation <> 1 and operation <> 2 and dateline < " + endTime;
				System.out.println(insert_sql);
				Statement ps = con.createStatement();
				ps.execute(insert_sql);
			}
			
		});
	}

	@Override
	public void deleteAccountLog(String partition, String code, long endTime) {
		Session session = getSession();
		String sql = "DELETE FROM " + partition + "_" + code + "_account_log WHERE operation <> 1 AND operation <> 2 AND dateline < ?";
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, endTime);
		query.executeUpdate();
	}
}
