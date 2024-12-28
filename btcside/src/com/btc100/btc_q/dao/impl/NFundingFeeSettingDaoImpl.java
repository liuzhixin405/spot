package com.btc100.btc_q.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.NFundingFeeSettingDao;
import com.btc100.btc_q.entity.NFundingFeeSetting;

@Repository("nFundingFeeSettingDao")
public class NFundingFeeSettingDaoImpl extends BaseDaoImpl<NFundingFeeSetting> implements NFundingFeeSettingDao<NFundingFeeSetting> {

	@Override
	public NFundingFeeSetting getFundingFeeSettingByCode(String partition, String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		
		final String sql = "select * from " + partition + "_funding_fee_setting where f_code = :code";
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("code", code);
		query.addEntity(NFundingFeeSetting.class);
		
		return (NFundingFeeSetting) getBySql(sql, params);
	}

	@Override
	public void createfundingFeeTables(final String partition, String code) {
		final String symbol = code;
		
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				
				final String fee_rate_sql = "CREATE TABLE IF NOT EXISTS " + partition + "_" + symbol + "_funding_fee_rate (" +
						"id INT(11) NOT NULL AUTO_INCREMENT," +
						"f_code VARCHAR(10) NOT NULL COMMENT '合约号'," + 
						"count_times INT(11) NOT NULL COMMENT '统计次数'," + 
						"fee_rate DECIMAL(18,8) NOT NULL COMMENT '资金费率'," +
						"dateline INT(11) NOT NULL COMMENT '更新时间'," +
						"done INT(5) NOT NULL DEFAULT 0 COMMENT '是否已执行'," +
						" PRIMARY KEY (id)," +
						" KEY idx_dateline (dateline) USING BTREE" +
						") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '资金费率表';";
				
				final String fee_record_sql = "CREATE TABLE IF NOT EXISTS " + partition + "_" + symbol + "_funding_fee_record (" +
						"id INT(11) NOT NULL AUTO_INCREMENT," +
						"f_code VARCHAR(10) NOT NULL COMMENT '合约号'," +
						"f_user_id INT(11) NOT NULL COMMENT '用户ID'," +
						"trans_type_id INT(5) NOT NULL DEFAULT 0 COMMENT '持仓方向'," +
						"curr_price DECIMAL(18,8) NOT NULL COMMENT '当前价格'," +
						"hold_count DECIMAL(18,8) NOT NULL COMMENT '持仓数量'," +
						"coefficient DECIMAL(18,8) NOT NULL DEFAULT 1 COMMENT '合约乘数（交易系数）'," +
						"position_value DECIMAL(18,8) NOT NULL COMMENT '持仓价值'," +
						"fee_rate DECIMAL(18,8) NOT NULL COMMENT '资金费率'," +
						"funding_fee DECIMAL(18,8) NOT NULL COMMENT '资金费'," +
						"price_change_fee DECIMAL(18,8) NOT NULL DEFAULT 0 COMMENT '价格变更抵扣费用'," +
						"dateline INT(11) NOT NULL COMMENT '时间'," +
						" PRIMARY KEY (id)," +
						" KEY idx_uid (f_user_id) USING BTREE" +
						") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '资金费记录表';";
				
				
				Statement ps = con.createStatement();
				ps.addBatch(fee_rate_sql);
				ps.addBatch(fee_record_sql);
				ps.executeBatch();
			}
		});
	}

	@Override
	public int insert(String partition, NFundingFeeSetting setting) {

		Session session = getSession();
		String sql = "insert into " + partition + "_funding_fee_setting "
				+ "(f_code,running,running_time,interest_rate_diff, divisor, clamp_up, clamp_down,"
				+ "fixed_fee_open,fixed_fee_rate) "
				+ "values (?,?,?,?,?,?,?,?,?)";

		SQLQuery query = session.createSQLQuery(sql);

		query.setString(0, setting.getF_code());
		query.setBoolean(1, setting.getRunning());
		query.setString(2, setting.getRunning_time());
		query.setBigDecimal(3, setting.getInterest_rate_diff());
		query.setBigDecimal(4, setting.getDivisor());
		query.setBigDecimal(5, setting.getClamp_up());
		query.setBigDecimal(6, setting.getClamp_down());
		query.setBoolean(7, setting.getFixed_fee_open());
		query.setBigDecimal(8, setting.getFixed_fee_rate());
		
		return query.executeUpdate();
	}
	
	@Override
	public int update(String partition, NFundingFeeSetting setting) {

		Session session = getSession();
		String sql = "update " + partition + "_funding_fee_setting set "
				+ "f_code=?,running=?,running_time=?,interest_rate_diff=?,divisor=?,clamp_up=?,clamp_down=?,"
				+ "fixed_fee_open=?,fixed_fee_rate=? where id=?";

		SQLQuery query = session.createSQLQuery(sql);

		query.setString(0, setting.getF_code());
		query.setBoolean(1, setting.getRunning());
		query.setString(2, setting.getRunning_time());
		query.setBigDecimal(3, setting.getInterest_rate_diff());
		query.setBigDecimal(4, setting.getDivisor());
		query.setBigDecimal(5, setting.getClamp_up());
		query.setBigDecimal(6, setting.getClamp_down());
		query.setBoolean(7, setting.getFixed_fee_open());
		query.setBigDecimal(8, setting.getFixed_fee_rate());
		query.setLong(9, setting.getId());
		
		return query.executeUpdate();
	}
}
