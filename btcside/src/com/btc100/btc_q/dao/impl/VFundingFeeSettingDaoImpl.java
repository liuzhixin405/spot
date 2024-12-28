package com.btc100.btc_q.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.VFundingFeeSettingDao;
import com.btc100.btc_q.entity.VFundingFeeSetting;

@Repository("vFundingFeeSettingDao")
public class VFundingFeeSettingDaoImpl<T> extends BaseDaoImpl<T> implements VFundingFeeSettingDao<T> {

	@Override
	public VFundingFeeSetting getFundingFeeSettingByCode(String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		final String hql = "from VFundingFeeSetting where f_code = :code";
		return (VFundingFeeSetting) get(hql, params);
	}

	@Override
	public void createfundingFeeTables(String code) {
		final String symbol = code;
		
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				
				final String fee_rate_sql = "CREATE TABLE IF NOT EXISTS " + symbol + "_v_funding_fee_rate (" +
						"id INT(11) NOT NULL AUTO_INCREMENT," +
						"f_code VARCHAR(10) NOT NULL COMMENT '合约号'," + 
						"count_times INT(11) NOT NULL COMMENT '统计次数'," + 
						"fee_rate DECIMAL(18,8) NOT NULL COMMENT '资金费率'," +
						"dateline INT(11) NOT NULL COMMENT '更新时间'," +
						"done INT(5) NOT NULL DEFAULT 0 COMMENT '是否已执行'," +
						" PRIMARY KEY (id)," +
						" KEY idx_dateline (dateline) USING BTREE" +
						") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '资金费率表';";
				
				final String fee_record_sql = "CREATE TABLE IF NOT EXISTS " + symbol + "_v_funding_fee_record (" +
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
}
