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

import com.btc100.btc_q.dao.VMemberTradeDetailDao;

@Repository("vMemberTradeDetailDao")
public class VMemberTradeDetailDaoImpl<VMemberTradeDetail> extends BaseDaoImpl<VMemberTradeDetail> implements VMemberTradeDetailDao<VMemberTradeDetail>{

	public com.btc100.btc_q.entity.VMemberTradeDetail insertTradeDetail(
			final com.btc100.btc_q.entity.VMemberTradeDetail tradeDetail) {
		getSession().doWork(new Work(){
			public void execute(Connection con) throws SQLException {
				PreparedStatement psmt = con.prepareStatement
	            ("insert into "+tradeDetail.getTransaction_code()+"_v_member_trade_detail (f_buy_trade_id,f_buy_uid,f_buy_user_email,f_sell_uid,f_sell_user_email,f_sell_trade_id," +
	            		"trade_buy_type_id,trade_sell_type_id,trade_flag,trade_trans_dirction,trade_price,trade_count,trade_time,transaction_id,transaction_code)" +
	            		"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
				long unix_time = getUnix_TIMESTAMP();
				psmt.setLong(1, tradeDetail.getF_buy_trade_id());
				psmt.setLong(2, tradeDetail.getF_buy_uid());
				psmt.setString(3, tradeDetail.getF_buy_user_email());
				psmt.setLong(4, tradeDetail.getF_sell_uid());
				psmt.setString(5, tradeDetail.getF_sell_user_email());
				psmt.setLong(6, tradeDetail.getF_sell_trade_id());
				psmt.setInt(7, tradeDetail.getTrade_buy_type_id());
				psmt.setInt(8, tradeDetail.getTrade_sell_type_id());
				psmt.setInt(9, tradeDetail.getTrade_flag());
				psmt.setInt(10, tradeDetail.getTrade_trans_dirction());
				psmt.setBigDecimal(11, tradeDetail.getTrade_price());
				psmt.setBigDecimal(12, tradeDetail.getTrade_count());
				psmt.setLong(13,unix_time );
				psmt.setLong(14, tradeDetail.getTransaction_id());
				psmt.setString(15, tradeDetail.getTransaction_code());
				psmt.executeUpdate();
				ResultSet results = psmt.getGeneratedKeys();
				long num = -1;
				if (results.next()) {
					num = results.getLong(1);
				}
				
				tradeDetail.setId(num);
				tradeDetail.setTrade_time(unix_time);
				
			}
			
		});
		
		return tradeDetail;
	}
	public List<Object[]> getAllTopN(String vCode, int n) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer(
				"select trade_time AS date, trade_price AS price, trade_count AS amount,id as tid, trade_trans_dirction AS tradType,trade_buy_type_id AS buyID,trade_sell_type_id AS SellID from "
						+ vCode + "_v_member_trade_detail order by id desc limit " + n);
        SQLQuery query = session.createSQLQuery(sql.toString());
		return query.list();
	}
	public List<Object[]> getAllTopN_123(String vCode, int n) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer(
				"select trade_time AS date, trade_price AS price, trade_count AS amount,id as tid, trade_trans_dirction AS tradType,trade_buy_type_id AS buyID,trade_sell_type_id AS SellID from "
						+ vCode + "_v_member_trade_detail order by id asc limit " + n);
        SQLQuery query = session.createSQLQuery(sql.toString());
		return query.list();
	}

	public List<Object[]> getAllTopN(String vCode, int n, long since) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer(
				"select trade_time AS date, trade_price AS price, trade_count AS amount,id as tid, trade_trans_dirction AS tradType,trade_buy_type_id AS buyID,trade_sell_type_id AS SellID from "
						+ vCode + "_v_member_trade_detail where id > " + since + " order by id asc limit " + n);
        SQLQuery query = session.createSQLQuery(sql.toString());
		return query.list();
	}

	@Override
	public void createMemberTradeDetailBak(final String code) {
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				
				final String create_table_sql = "create table if not exists " + code + "_v_member_trade_detail_bak like " + code + "_v_member_trade_detail;";
				
				Statement ps = con.createStatement();
				ps.execute(create_table_sql);
			}
			
		});
	}

	@Override
	public void insertMemberTradeDetailBak(final String code, final long endTime) {
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				
				String insert_sql = "insert into " + code + "_v_member_trade_detail_bak select * from " + code + "_v_member_trade_detail"
						+ " where trade_time < " + endTime;
				System.out.println(insert_sql);
				Statement ps = con.createStatement();
				ps.execute(insert_sql);
			}

		});
	}
	
	@Override
	public void deleteMemberTradeDetail(String code, long endTime) {
		Session session = getSession();
		String sql = "DELETE FROM " + code + "_v_member_trade_detail WHERE trade_time < ?";
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, endTime);
		query.executeUpdate();
	}	
}
