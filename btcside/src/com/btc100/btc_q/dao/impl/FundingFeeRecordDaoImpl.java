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

import com.btc100.btc_q.dao.FundingFeeRecordDao;
import com.btc100.btc_q.entity.FundingFeeRecord;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

@Repository("fundingFeeRecordDao")
public class FundingFeeRecordDaoImpl<T> extends BaseDaoImpl<T> implements FundingFeeRecordDao<T> {

	@Override
	public FundingFeeRecord insertRecord(final FundingFeeRecord record) {
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				PreparedStatement psmt = con.prepareStatement(
						"insert into "
								+ record.getF_code()
								+ "_funding_fee_record (f_code, f_user_id, trans_type_id, curr_price, hold_count, coefficient, position_value, fee_rate, " +
										"funding_fee, price_change_fee, dateline)"
								+ "values (?,?,?,?,?,?,?,?,?,?,?)",
						Statement.RETURN_GENERATED_KEYS);
				psmt.setString(1, record.getF_code());
				psmt.setLong(2, record.getF_user_id());
				psmt.setInt(3, record.getTrans_type_id());
				psmt.setBigDecimal(4, record.getCurr_price());
				psmt.setBigDecimal(5, record.getHold_count());
				psmt.setBigDecimal(6, record.getCoefficient());
				psmt.setBigDecimal(7, record.getPosition_value());
				psmt.setBigDecimal(8, record.getFee_rate());
				psmt.setBigDecimal(9, record.getFunding_fee());
				psmt.setBigDecimal(10, record.getPrice_change_fee());
				psmt.setLong(11, record.getDateline());
				
				psmt.executeUpdate();
				ResultSet results = psmt.getGeneratedKeys();
				long num = -1;
				if (results.next()) {
					num = results.getLong(1);
				}

				record.setId(num);
			}

		});

		return record;
	}

	@Override
	public BigDecimal sumFundingFee(String symbol, long userId) {
		if (isTableExist(symbol)) {
			String sql="SELECT ifnull(SUM(t.funding_fee + t.price_change_fee), 0) from " 
					+ symbol + "_funding_fee_record t where t.f_user_id = :userId";
			Session session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter("userId", userId);
			return (BigDecimal) query.uniqueResult();
		} else {
			return BigDecimal.ZERO;
		}
	}
	
	private boolean isTableExist(String symbol) {
		Session session = getSession();
		String sql = "select count(*) from information_schema.TABLES t where t.TABLE_NAME = '" + symbol + "_funding_fee_record'";
		SQLQuery query = session.createSQLQuery(sql);
		int count = ((BigInteger) query.uniqueResult()).intValue();
		return count > 0;
	}

	@Override
	public PagerModel findBySymbol(String symbol, long uid) {
		Session session = getSession();
		
		String sql = "select * from " + symbol + "_funding_fee_record where 1 = 1";
		String countSql = "select count(1) from " + symbol + "_funding_fee_record where 1 = 1";
		
		if (uid > 0) {
			sql += " and f_user_id = :uid";
			countSql += " and f_user_id = :uid";
		}

		sql += " ORDER BY id DESC LIMIT " + SystemContext.getOffset() + "," + SystemContext.getPageSize();
		
		SQLQuery query = session.createSQLQuery(sql);
		if (uid > 0) {
			query.setLong("uid", uid);
		}
		
		SQLQuery countQuery = session.createSQLQuery(countSql);
		if (uid > 0) {
			countQuery.setLong("uid", uid);
		}
		
		query.addEntity(FundingFeeRecord.class);
		
		PagerModel pm = new PagerModel();
		pm.setDatas(query.list());
		pm.setTotal(ConvertUtil.toInt(countQuery.uniqueResult(), 0));
		
		return pm;
	}

}
