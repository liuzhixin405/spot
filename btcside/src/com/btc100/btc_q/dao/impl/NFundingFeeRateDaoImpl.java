package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.NFundingFeeRateDao;
import com.btc100.btc_q.entity.NFundingFeeRate;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

@Repository("nFundingFeeRateDao")
public class NFundingFeeRateDaoImpl extends BaseDaoImpl<NFundingFeeRate> implements NFundingFeeRateDao<NFundingFeeRate> {

	@Override
	public NFundingFeeRate getByTime(String partition, String symbol, long time) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("select * from " + partition + "_" + symbol + "_funding_fee_rate where dateline = :time LIMIT 1");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setLong("time", time);
		query.addEntity(NFundingFeeRate.class);
		return (NFundingFeeRate) query.uniqueResult();
	}

	@Override
	public int updateByTime(String partition, String symbol, BigDecimal feeRate, long time) {
		Session session = getSession();
		String sql = "update " + partition + "_" + symbol + "_funding_fee_rate set fee_rate = (fee_rate * count_times + :rate) / (count_times + 1.0),"
				+ "count_times = count_times + 1 where dateline = :time";
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("time", time);
		query.setBigDecimal("rate", feeRate);
		
		return query.executeUpdate();
	}

	@Override
	public NFundingFeeRate saveFeeRate(final String partition, final NFundingFeeRate feeRate) {
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				PreparedStatement psmt = con
						.prepareStatement(
								"insert into "
										+ partition + "_" + feeRate.getF_code()
										+ "_funding_fee_rate (f_code,count_times,fee_rate,dateline)"
										+ "values (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
				psmt.setString(1, feeRate.getF_code());
				psmt.setInt(2, feeRate.getCount_times());
				psmt.setBigDecimal(3, feeRate.getFee_rate());
				psmt.setLong(4, feeRate.getDateline());
				psmt.executeUpdate();
				ResultSet results = psmt.getGeneratedKeys();
				long num = -1;
				if (results.next()) {
					num = results.getLong(1);
				}
				feeRate.setId(num);
			}
		});

		return feeRate;
	}

	@Override
	public int clampByTime(String partition, String symbol, BigDecimal feeRate, long time) {
		Session session = getSession();
		String sql = "update " + partition + "_" + symbol + "_funding_fee_rate set fee_rate = :rate,"
				+ "done=:done where dateline = :time";
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("time", time);
		query.setInteger("done", 1);
		query.setBigDecimal("rate", feeRate);
		
		return query.executeUpdate();
	}

	@Override
	public PagerModel findBySymbol(String partition, String symbol) {
		Session session = getSession();
		
		String sql = "select * from " + partition + "_" + symbol + "_funding_fee_rate WHERE done = 1";
		String countSql = "select count(1) from " + partition + "_" + symbol + "_funding_fee_rate WHERE done = 1";

		sql += " ORDER BY id DESC LIMIT " + SystemContext.getOffset() + "," + SystemContext.getPageSize();
		
		SQLQuery query = session.createSQLQuery(sql);
		SQLQuery countQuery = session.createSQLQuery(countSql);
		query.addEntity(NFundingFeeRate.class);
		
		PagerModel pm = new PagerModel();
		pm.setDatas(query.list());
		pm.setTotal(ConvertUtil.toInt(countQuery.uniqueResult(), 0));
		
		return pm;
	}

	@Override
	public NFundingFeeRate getLast(String partition, String symbol) {
		if (!isTableExist(partition, symbol)) {
			return null;
		}
		
		Session session = getSession();
		StringBuffer sql = new StringBuffer("select * from " + partition + "_" + symbol + "_funding_fee_rate order by id desc LIMIT 1");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.addEntity(NFundingFeeRate.class);
		
		return (NFundingFeeRate) query.uniqueResult();
	}

	private boolean isTableExist(String partition, String symbol) {
		Session session = getSession();
		String sql = "select count(*) from information_schema.TABLES t where t.TABLE_NAME = '" + partition + "_" + symbol + "_funding_fee_rate'";
		SQLQuery query = session.createSQLQuery(sql);
		int count = ((BigInteger) query.uniqueResult()).intValue();
		return count > 0;
	}
}
