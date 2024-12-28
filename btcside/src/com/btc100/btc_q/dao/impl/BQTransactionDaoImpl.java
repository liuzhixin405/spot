package com.btc100.btc_q.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQTransactionDao;

@Repository("bqTransactionDao")
public class BQTransactionDaoImpl<T> extends BaseDaoImpl<T> implements BQTransactionDao<T> {

	public void createTransactionRelated(final String code) {
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				
				final String trade_sql = "CREATE TABLE IF NOT EXISTS " + code + "_member_trade (" +
				"id int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '会员与交易对应关系ID'," +
				"f_user_id int(10) unsigned NOT NULL COMMENT '用户ID'," +
				"trade_id int(12) unsigned NOT NULL DEFAULT '0' COMMENT '交易ID'," +
				"dateline int(10) unsigned NOT NULL DEFAULT '0' COMMENT '交易时间'," +
				"  PRIMARY KEY (id)," +
				"  UNIQUE KEY uniq_user_trade (f_user_id,trade_id,dateline) USING BTREE," +
				"  KEY idx_dateline (dateline)" +
				") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会员交易记录关系表(多对多),一个交易，需要将进行这个交易的双方与交易的对应关系都写入此表一份';";
				
				final String trade_detail_sql = "CREATE TABLE IF NOT EXISTS " + code + "_member_trade_detail (" +
				"id int(12) unsigned NOT NULL AUTO_INCREMENT," +
				"f_buy_trade_id int(12) unsigned NOT NULL COMMENT '买方对应的交易ID'," +
				"f_buy_uid int(10) unsigned NOT NULL DEFAULT '0' COMMENT '买家uid'," +
				"f_buy_user_email char(40) NOT NULL DEFAULT '' COMMENT '买家用户邮箱'," +
				"f_sell_uid int(10) unsigned NOT NULL DEFAULT '0' COMMENT '卖家uid'," +
				"f_sell_user_email char(40) NOT NULL DEFAULT '' COMMENT '卖家用户邮箱'," +
				"f_sell_trade_id int(10) unsigned NOT NULL COMMENT '卖方对应的交易id'," +
				"trade_buy_type_id smallint(5) NOT NULL DEFAULT '1' COMMENT '成交类型ID(1:多单开仓/2:多单平仓/3:空头开仓/4:空头平仓，见_trade_log:trade_type_id)'," +
				"trade_sell_type_id smallint(5) NOT NULL DEFAULT '1' COMMENT '同上'," +
				"trade_flag smallint(5) NOT NULL DEFAULT '1' COMMENT '交易类型标识(1:普通交易, 2:交割 3:强平)'," +
				"trade_trans_dirction smallint(5) NOT NULL DEFAULT '1' COMMENT '这笔交易的成交方向(1:买 2:卖)'," +
				"trade_price decimal(18,8) NOT NULL DEFAULT '0.00' COMMENT '成交价格'," +
				"trade_count decimal(18,8) NOT NULL DEFAULT '0' COMMENT '成交数量'," +
				"trade_time int(10) unsigned NOT NULL COMMENT '成交时间,unix时间格式'," +
				"transaction_id smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '合约号id'," +
				"transaction_code varchar(10) NOT NULL DEFAULT '' COMMENT '合约号'," +
				" PRIMARY KEY (id)," +
				" KEY idx_tradetime (trade_time) USING BTREE," +
				" KEY idx_fbuyuid_fselluid_tradetime (f_buy_uid, f_sell_uid, trade_time) USING BTREE" +
				") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='交易详情表';";
				
				final String delegation_sql = "CREATE TABLE IF NOT EXISTS " + code + "_pending_delegation (" +
				"delegation_id int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '委托编号'," +
				"pending_no bigint(19) NOT NULL COMMENT '挂单流水号'," +
				"delegation_time int(10) unsigned NOT NULL DEFAULT '0' COMMENT '委托时间,unix时间格式'," +
				"transaction_id smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '合约号id'," +
				"transaction_code varchar(10) NOT NULL DEFAULT '' COMMENT '合约号'," +
				"direction smallint(2) NOT NULL DEFAULT '0' COMMENT '操作方向(1:买 2:卖)'," +
				"delegation_count decimal(18,8) unsigned NOT NULL DEFAULT '0.00' COMMENT '委托数量'," +
				"delegation_price decimal(18,8) unsigned NOT NULL DEFAULT '0.00' COMMENT '委托价格'," +
				"deal_count decimal(18,8) NOT NULL DEFAULT '0.00' COMMENT '已成交数量'," +
				"undeal_count decimal(18,8) NOT NULL DEFAULT '0.00' COMMENT '未成交数量'," +
				"multiple smallint(4) unsigned NOT NULL DEFAULT '1' COMMENT '杠杆'," +
				"deposit decimal(18,8) NOT NULL DEFAULT '0.00' COMMENT '保证金'," +
				"commission_rate decimal(18,8) NOT NULL DEFAULT '0.00' COMMENT '佣金比例'," +
				"priority smallint(2) unsigned NOT NULL DEFAULT '0' COMMENT '挂单成交优先级'," +
				"f_user_id int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID'," +
				"flag smallint(5) NOT NULL DEFAULT '0' COMMENT '0默认，1交易中，2撤单中（其他）'," +
				"f_fromremain decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '保证金来源于余额的量',"+
				"`f_enforce_price` decimal(18,8) NOT NULL COMMENT '爆仓合约均价',"+
				"`f_frozenremain` decimal(18,8) NOT NULL COMMENT '冻结备用金',"+
				" PRIMARY KEY (`delegation_id`), KEY `idx_f_user_id` (`f_user_id`), KEY `idx_direction` (`direction`) USING BTREE"+
				") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='委托挂单表（非全部成交）';";
				
				final String delegation_cache_sql = "CREATE TABLE IF NOT EXISTS " + code + "_pending_delegation_cache (" +
				"delegation_id int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '委托编号'," + 
				"pending_no bigint(19) NOT NULL COMMENT '挂单流水号'," +
				"delegation_time int(10) unsigned NOT NULL COMMENT '委托时间,unix时间格式'," + 
				"transaction_id smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '合约号id'," + 
				"transaction_code varchar(10) NOT NULL DEFAULT '' COMMENT '合约号'," + 
				"direction smallint(2) NOT NULL DEFAULT '0' COMMENT '操作方向'," + 
				"delegation_count decimal(18,8) unsigned NOT NULL DEFAULT '0.00' COMMENT '委托数量'," + 
				"delegation_price decimal(18,8) unsigned NOT NULL DEFAULT '0' COMMENT '委托价格'," + 
				"deal_count decimal(18,8) NOT NULL DEFAULT '0.00' COMMENT '已成交数量'," + 
				"undeal_count decimal(18,8) NOT NULL DEFAULT '0.00' COMMENT '未成交数量'," + 
				"multiple smallint(4) unsigned NOT NULL DEFAULT '1' COMMENT '杠杆'," + 
				"deposit decimal(18,8) NOT NULL DEFAULT '0.00' COMMENT '保证金'," + 
				"commission_rate decimal(18,8) NOT NULL DEFAULT '0.00' COMMENT '佣金比例'," +
				"priority smallint(2) unsigned NOT NULL COMMENT '挂单成交优先级(1:开仓, 2:平仓, 3:爆仓)'," + 
				"f_user_id int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID'," + 
				"flag smallint(5) NOT NULL DEFAULT '0' COMMENT '0默认，1交易中，2撤单中（其他）'," +
				"f_fromremain decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '保证金来源于余额的量',"+
				"`f_enforce_price` decimal(18,8) NOT NULL COMMENT '爆仓合约均价',"+
				"`f_frozenremain` decimal(18,8) NOT NULL COMMENT '冻结备用金',"+
				" PRIMARY KEY (delegation_id),"+
				"KEY idx_f_user_id (f_user_id)" +
				") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='委托挂单表（非全部成交）';";
				
				final String plan_delegation_sql = "CREATE TABLE IF NOT EXISTS " + code + "_plan_pending_delegation (" +
				  "id INT(11) NOT NULL AUTO_INCREMENT," + 
				  "f_code VARCHAR(45) NOT NULL DEFAULT '' COMMENT '合约号'," + 
				  "f_user_id INT(11) NOT NULL DEFAULT '0' COMMENT '用户ID'," + 
				  "order_type SMALLINT(5) NOT NULL DEFAULT '0' COMMENT '挂单类型（0：限价委托;1：市价委托）'," + 
				  "tri_price DECIMAL(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '触发价格'," + 
				  "delegation_price DECIMAL(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '委托价格'," + 
				  "delegation_count DECIMAL(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '委托数量'," + 
				  "delegation_time INT(10) NOT NULL DEFAULT 0 COMMENT '委托时间'," + 
				  "multiple SMALLINT(4) NOT NULL DEFAULT '1' COMMENT '杆杠倍数'," + 
				  "direction SMALLINT(4) NOT NULL DEFAULT '0' COMMENT '操作方向'," + 
				  "type SMALLINT(4) NOT NULL DEFAULT '1' COMMENT '类型（1-现价大于触发价时进行委托，2-现价低于触发价时进行委托）'," + 
				  "status SMALLINT(4) NOT NULL DEFAULT '0' COMMENT '状态（0-未生效，1-已生效，2-失败，3-已撤销）'," + 
				  "note VARCHAR(64) NOT NULL DEFAULT '' COMMENT '备注'," + 
				  "trade_type SMALLINT(4) NOT NULL DEFAULT '0' COMMENT '挂单类型'," + 
				  "transaction_member_id INT(11) NOT NULL DEFAULT '0' COMMENT '持仓ID'," + 
				  "stop_loss_type_id SMALLINT(4) NOT NULL DEFAULT '0' COMMENT '1-止盈，2-止损'," + 
				  "PRIMARY KEY (id), " + 
				  "KEY idx_fuserid_delegationtime (f_user_id,delegation_time) USING BTREE, " + 
				  "KEY idx_triprice_type (tri_price,type) USING BTREE " + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '计划委托表';";
				
				final String pending_log_sql = "CREATE TABLE IF NOT EXISTS " + code + "_pending_log ("+
				"log_id int(12) unsigned NOT NULL AUTO_INCREMENT,"+
				"delegation_id int(10) unsigned NOT NULL DEFAULT '0' COMMENT '委托编号',"+
				"pending_no bigint(19) NOT NULL COMMENT '挂单流水号'," +
				"delegation_time int(10) unsigned NOT NULL COMMENT '委托时间,unix时间格式',"+
				"transaction_id smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '合约号id',"+
				"transaction_code varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',"+
				"trade_type_id smallint(2) NOT NULL DEFAULT '0' COMMENT '操作类型(1:多单开仓/2:多单平仓/3:空头开仓/4:空头平仓)',"+
				"delegation_count decimal(18,8) unsigned NOT NULL DEFAULT '0.00' COMMENT '委托数量',"+
				"delegation_price decimal(18,8) unsigned NOT NULL DEFAULT '0.00' COMMENT '委托价格',"+
				"multiple smallint(4) unsigned NOT NULL DEFAULT '0' COMMENT '杠杆',"+
				"deal_count decimal(18,8) unsigned NOT NULL DEFAULT '0.00' COMMENT '已成交数量',"+
				"undeal_count decimal(18,8) NOT NULL DEFAULT '0.00' COMMENT '未成交数量',"+
				"deposit decimal(18,8) NOT NULL DEFAULT '0.00' COMMENT '保证金',"+
				"action smallint(2) NOT NULL DEFAULT '1' COMMENT '1:成交/2:结算/3:强平/4:撤单',"+
				"dateline int(10) unsigned NOT NULL COMMENT '操作时间,unix时间格式',"+
				"f_user_id int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',"+
				"avg_price decimal(18,8) unsigned NOT NULL DEFAULT '0.00' COMMENT '成交均价',"+
				"income decimal(18,8) NOT NULL DEFAULT '0.00' COMMENT '盈亏',"+
				"fee decimal(18,8) NOT NULL DEFAULT '0.00' COMMENT '手续费',"+
				"bfx_fee decimal(18,8) NOT NULL DEFAULT '0.00' COMMENT 'bfx抵扣手续费',"+
				"device smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '下单设备（0-PC，1-App，2-Api）',"+
				"ip_addr char(15) NOT NULL DEFAULT '' COMMENT '下单IP',"+
				"flag smallint(5) NOT NULL DEFAULT '0' COMMENT '0表示正常挂单日志，1标识交易异常时转过来的挂单'," +
				"  PRIMARY KEY (log_id),"+
				"  KEY idx_f_user_id (f_user_id),"+
				"  KEY idx_pendingno_fuserid_dateline (pending_no,f_user_id,dateline) USING BTREE"+
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
				
				final String trade_log_sql = "CREATE TABLE IF NOT EXISTS " + code + "_trade_log ("+
				"trade_id int(12) unsigned NOT NULL AUTO_INCREMENT COMMENT '交易ID',"+
				"transaction_id smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '合约号id',"+
				"transaction_code varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',"+
				"trade_type_id smallint(2) unsigned NOT NULL DEFAULT '0' COMMENT '成交类型(1:多单开仓/2:多单平仓/3:空头开仓/4:空头平仓/5:多单爆仓/6:空单爆仓)',"+
				"trade_trans_dirction smallint(2) unsigned NOT NULL DEFAULT '0' COMMENT '这笔交易的成交方向',"+
				"hold_price decimal(18,8) unsigned NOT NULL DEFAULT '0.000' COMMENT '持仓价(平/爆仓单)',"+
				"trade_average_price decimal(18,8) unsigned NOT NULL DEFAULT '0.000' COMMENT '成交均价',"+
				"trade_count decimal(18,8) unsigned NOT NULL DEFAULT '0.000' COMMENT '成交数量',"+
				"trade_multiple smallint(2) unsigned NOT NULL DEFAULT '1' COMMENT '杠杆',"+
				"trade_deposit decimal(18,8) unsigned NOT NULL DEFAULT '0.0000' COMMENT '保证金',"+
				"trade_income decimal(18,8) NOT NULL DEFAULT '0.000' COMMENT '盈亏额',"+
				"trade_fee decimal(18,8) unsigned NOT NULL DEFAULT '0.000000' COMMENT '手续费',"+
				"trade_bfx_fee decimal(18,8) unsigned NOT NULL DEFAULT '0.000000' COMMENT 'bfx抵扣手续费',"+
				"f_user_id int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',"+
				"trade_time int(10) unsigned NOT NULL DEFAULT '0' COMMENT '成交时间,unix时间格式',"+
				"pending_no bigint(19) unsigned NOT NULL DEFAULT '0' COMMENT '对应挂单流水号'," +
				" PRIMARY KEY (trade_id),"+
				" KEY idx_trade_time (trade_time,trade_type_id) USING BTREE,"+
				" KEY idx_fuserid_tradetime (f_user_id,trade_time) USING BTREE,"+
				" KEY idx_tradetime_count_price (trade_time,trade_count,trade_average_price) USING BTREE"+
				") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='交易记录日志表。一个交易会产生两个用户对这个交易的操作记录，基本一份是更新，另一份是写入新记录';";
				
				final String account_log_sql = "CREATE TABLE IF NOT EXISTS " + code + "_account_log ("+ 
				  "log_id int(10) unsigned NOT NULL AUTO_INCREMENT,"+
				  "title varchar(100) NOT NULL DEFAULT '' COMMENT '描述标题',"+
				  "currency_type_id tinyint(11) unsigned NOT NULL DEFAULT '0' COMMENT '币种（btc/ltc,gtc)1:btc 2:ltc 3...(后期可添加币种)',"+
				  "operation tinyint(2) unsigned NOT NULL DEFAULT '0' COMMENT '操作类型(0:其它/1:转入/2:转出/3:交易/4:追加保证金)',"+
				  "direction tinyint(1) NOT NULL DEFAULT '0' COMMENT '1:加款 -1:减款 ',"+
				  "current_amount decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '当前账户余额(bq_acount:btc/bq_account:ltc)',"+
				  "change_amount decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '增减',"+
				  "lastest_amount decimal(18,8) NOT NULL COMMENT '变更后的金额',"+
				  "before_frozen decimal(18,8) NOT NULL COMMENT '变更前的冻结',"+
				  "after_frozen decimal(18,8) NOT NULL COMMENT '变更后的冻结',"+
				  "dateline int(10) unsigned NOT NULL DEFAULT '0' COMMENT '操作时间,unix时间格式',"+
				  "f_user_id int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',"+
				  "trans_no char(20) NOT NULL DEFAULT '' COMMENT '交易流水号',"+
				  "record_id int(10) unsigned NOT NULL DEFAULT '0' COMMENT '交易分表id',"+
				  "f_code varchar(10) NOT NULL,"+
				  "PRIMARY KEY (`log_id`),"+
				  "UNIQUE KEY `uniq_trans_no` (`trans_no`),"+
				  "KEY `idx_f_user_id` (`f_user_id`,`currency_type_id`) USING BTREE"+
				") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户提现/充值记录,主要记录合约中用户金额变更(个人财务流水）';";
				
				final String stop_los_log_sql = "CREATE TABLE IF NOT EXISTS " + code + "_stop_loss_log (" + 
				  "`log_id` int(12) unsigned NOT NULL AUTO_INCREMENT," + 
				  "`transaction_id` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '合约号ID'," + 
				  "`transaction_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号'," + 
				  "`trade_type_id` smallint(6) NOT NULL DEFAULT '1' COMMENT '1 买多, 2卖空 (成交类型1:多单开仓,买多/2:空头开仓,卖空)'," + 
				  "`f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID'," + 
				  "`dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '添加记录时间'," + 
				  "`action` smallint(2) NOT NULL DEFAULT '1' COMMENT '1:成交/2:结算/3:强平/4:撤单'," + 
				  "`remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注原因'," + 
				  "`stop_loss_type_id` smallint(6) NOT NULL DEFAULT '1' COMMENT '1止盈 2止损'," + 
				  "`transaction_member_id` int(11) NOT NULL DEFAULT '0' COMMENT '持仓ID'," + 
				  "`stop_loss_price` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '止损价'," + 
				  "`pending_trans_price` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '委托价'," + 
				  "PRIMARY KEY (`log_id`)" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='止盈止损日志表';";
				
				final String ksql1 = "CREATE TABLE IF NOT EXISTS ";
				final String ksql2 = " (id int(12) unsigned NOT NULL AUTO_INCREMENT,"
						+ "code varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',"
						+ "high decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '最高价',"
						+ "low decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '最低价',"
						+ "first decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',"
						+ "last decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',"
						+ "vol decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',"
						+ "time int(10) unsigned NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',"
						+ "PRIMARY KEY (id),"
						+ "KEY idx_time (time) USING BTREE"
						+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
				final String one_minute_trade_sql = ksql1 + code + "_one_minute_trade" + ksql2;
				final String three_minutes_trade_sql = ksql1 + code + "_three_minutes_trade" + ksql2;
				final String fifteen_minutes_trade_sql = ksql1 + code + "_fifteen_minutes_trade" + ksql2;
				final String half_hour_trade_sql = ksql1 + code + "_half_hour_trade" + ksql2;
				final String one_hour_trade_sql = ksql1 + code + "_one_hour_trade" + ksql2;
				final String two_hours_trade_sql = ksql1 + code + "_two_hours_trade" + ksql2;
				final String four_hours_trade_sql = ksql1 + code + "_four_hours_trade" + ksql2;
				final String six_hours_trade_sql = ksql1 + code + "_six_hours_trade" + ksql2;
				final String twelve_hours_trade_sql = ksql1 + code + "_twelve_hours_trade" + ksql2;
				final String five_minutes_trade_sql = ksql1 + code + "_five_minutes_trade" + ksql2;
				final String one_day_trade_sql = ksql1 + code + "_one_day_trade" + ksql2;
				final String one_week_trade_sql = ksql1 + code + "_one_week_trade" + ksql2;
				final String one_month_trade_sql = ksql1 + code + "_one_month_trade" + ksql2;
				
				Statement ps = con.createStatement();
				ps.addBatch(trade_sql);
				ps.addBatch(trade_detail_sql);
				ps.addBatch(delegation_sql);
				ps.addBatch(delegation_cache_sql);
				ps.addBatch(plan_delegation_sql);
				ps.addBatch(pending_log_sql);
				ps.addBatch(trade_log_sql);
				ps.addBatch(account_log_sql);
				ps.addBatch(stop_los_log_sql);
				ps.addBatch(one_minute_trade_sql);
				ps.addBatch(three_minutes_trade_sql);
				ps.addBatch(fifteen_minutes_trade_sql);
				ps.addBatch(half_hour_trade_sql);
				ps.addBatch(one_hour_trade_sql);
				ps.addBatch(two_hours_trade_sql);
				ps.addBatch(four_hours_trade_sql);
				ps.addBatch(six_hours_trade_sql);
				ps.addBatch(twelve_hours_trade_sql);
				ps.addBatch(five_minutes_trade_sql);
				ps.addBatch(one_day_trade_sql);
				ps.addBatch(one_week_trade_sql);
				ps.addBatch(one_month_trade_sql);
				ps.executeBatch();
			}

		});
	}
	
	public T findTranByCode(String code){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return super.get(" from BQTransaction t where t.transaction_code = :code ", params);
	}

}
