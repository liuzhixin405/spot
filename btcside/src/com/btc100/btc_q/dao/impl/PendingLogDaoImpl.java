package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.PendingLogDao;

@Repository("pendingLogDao")
public class PendingLogDaoImpl<PendingLog> extends BaseDaoImpl<PendingLog> implements PendingLogDao<PendingLog> {

	public void insertLog(com.btc100.btc_q.entity.PendingLog log) {
		Session session = getSession();
		String sql = "insert into " + log.getTransaction_code() + "_pending_log"
				+ " (delegation_id,pending_no,delegation_time,transaction_id,transaction_code,trade_type_id,delegation_count,"
				+ "delegation_price,multiple,deal_count,undeal_count,deposit,action,dateline,f_user_id,avg_price,device,ip_addr,flag) "
				+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, log.getDelegation_id());
		query.setLong(1, log.getPending_no());
		query.setLong(2, log.getDelegation_time());
		query.setLong(3, log.getTransaction_id());
		query.setString(4, log.getTransaction_code());
		query.setInteger(5, log.getTrade_type_id());
		query.setBigDecimal(6, log.getDelegation_count());
		query.setBigDecimal(7, log.getDelegation_price());
		query.setInteger(8, log.getMultiple());
		query.setBigDecimal(9, log.getDeal_count());
		query.setBigDecimal(10, log.getUndeal_count());
		query.setBigDecimal(11, log.getDeposit());
		query.setInteger(12, log.getAction());
		query.setLong(13, getUnix_TIMESTAMP());
		query.setLong(14, log.getF_user_id());
		query.setBigDecimal(15, log.getAvg_price());
		query.setInteger(16, log.getDevice());
		query.setString(17, log.getIp_addr());
		query.setInteger(18, log.getFlag());
		
		query.executeUpdate();
	}

	public int jsCancelOrder(String bqCode, int action, int pripority, int direction) {
		String sql = "INSERT INTO "
				+ bqCode
				+ "_pending_log(delegation_id, pending_no, delegation_time, transaction_id, transaction_code, trade_type_id, delegation_count, delegation_price, multiple, deal_count, undeal_count, deposit, action, dateline, f_user_id, flag)"
				+ "SELECT delegation_id,pending_no, delegation_time, transaction_id, transaction_code," + action
				+ ",delegation_count, delegation_price,deal_count, undeal_count,multiple,deposit,2,UNIX_TIMESTAMP(),f_user_id,flag FROM " + bqCode
				+ "_pending_delegation WHERE direction=" + direction + " AND priority=" + pripority;

		SQLQuery query = getSession().createSQLQuery(sql);
		return query.executeUpdate();
	}

	public int jsCancelOrderForCache(String bqCode, int action, int pripority, int direction) {
		String sql = "INSERT INTO "
				+ bqCode
				+ "_pending_log(delegation_id, pending_no, delegation_time, transaction_id, transaction_code, trade_type_id, delegation_count, delegation_price, multiple, deal_count, undeal_count, deposit, action, dateline, f_user_id, flag)"
				+ "SELECT delegation_id,pending_no, delegation_time, transaction_id, transaction_code," + action
				+ ",delegation_count, delegation_price,deal_count, undeal_count,multiple,deposit,2,UNIX_TIMESTAMP(),f_user_id,flag FROM " + bqCode
				+ "_pending_delegation_cache WHERE direction=" + direction + " AND priority=" + pripority;

		SQLQuery query = getSession().createSQLQuery(sql);
		return query.executeUpdate();
	}

	@Override
	public void createPendingLogBak(final String code) {
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				
				final String create_table_sql = "create table if not exists " + code + "_pending_log_bak like " + code + "_pending_log;";
				
				Statement ps = con.createStatement();
				ps.execute(create_table_sql);
			}
			
		});
	}

	@Override
	public void insertPendingLogBak(final String code, final long endTime) {
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				
				String insert_sql = "insert into " + code + "_pending_log_bak select * from " + code + "_pending_log"
						+ " where delegation_time < " + endTime;
				System.out.println(insert_sql);
				Statement ps = con.createStatement();
				ps.execute(insert_sql);
			}

		});
	}

	@Override
	public void deletePendingLog(String code, long endTime) {
		Session session = getSession();
		String sql = "DELETE FROM " + code + "_pending_log WHERE delegation_time < ?";
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, endTime);
		query.executeUpdate();
	}

	@Override
	public void updateActionByPendingNo(String bqCode, long pendingNo, long userId, int action) {
		Session session = getSession();
		String sql = "update " + bqCode + "_pending_log set action = :action, dateline = unix_timestamp() where pending_no = :pendingNo and f_user_id = :userId";
		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger("action", action);
		query.setLong("pendingNo", pendingNo);
		query.setLong("userId", userId);
		query.executeUpdate();
	}

	@Override
	public void updateUndealByPendingNo(String bqCode, long pendingNo, long userId, BigDecimal price, BigDecimal dealCount,
			BigDecimal income, BigDecimal fee, BigDecimal bfxFee) {
		if (dealCount.compareTo(BigDecimal.ZERO) <= 0 || price.compareTo(BigDecimal.ZERO) <= 0) return;
		
		Session session = getSession();
		String sql = "update " + bqCode + "_pending_log set avg_price=((deal_count*avg_price + :dealCount*:price)/(deal_count+:dealCount)),deal_count=deal_count+:dealCount,";
		
		if (income.compareTo(BigDecimal.ZERO) != 0) {
			sql += "income=income+:income,";
		}
		if (fee.compareTo(BigDecimal.ZERO) != 0) {
			sql += "fee=fee+:fee,";
		}
		if (bfxFee.compareTo(BigDecimal.ZERO) != 0) {
			sql += "bfx_fee=bfx_fee+:bfxFee,";
		}
		
		sql += "undeal_count=undeal_count-:dealCount,action=(case when undeal_count = 0 then 1 else 0 end),dateline=unix_timestamp() where pending_no = :pendingNo and f_user_id = :userId";
		SQLQuery query = session.createSQLQuery(sql);
		query.setBigDecimal("dealCount", dealCount);
		query.setBigDecimal("price", price);
		query.setLong("pendingNo", pendingNo);
		query.setLong("userId", userId);
		
		if (income.compareTo(BigDecimal.ZERO) != 0) {
			query.setBigDecimal("income", income);
		}
		if (fee.compareTo(BigDecimal.ZERO) != 0) {
			query.setBigDecimal("fee", fee);
		}
		if (bfxFee.compareTo(BigDecimal.ZERO) != 0) {
			query.setBigDecimal("bfxFee", bfxFee);
		}
		
		query.executeUpdate();
	}

}
