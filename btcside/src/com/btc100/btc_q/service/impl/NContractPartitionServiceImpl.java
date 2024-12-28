package com.btc100.btc_q.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.NContractPartitionDao;
import com.btc100.btc_q.entity.NContractPartition;
import com.btc100.btc_q.service.NContractPartitionService;
import com.btc100.btc_q.vo.PagerModel;

@Service("nContractPartitionService")
public class NContractPartitionServiceImpl implements NContractPartitionService {

	@Autowired
	private NContractPartitionDao<NContractPartition> nContractPartitionDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	
	@Override
	public void update(final NContractPartition o) {
		nContractPartitionDao.save(o);
		
		nContractPartitionDao.getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				Statement ps = con.createStatement();
				
				String account_contract_sql = "create table if not exists " + o.getPart() + "_account_contract like bq_account_contract";
				String account_deposit_log_sql = "create table if not exists " + o.getPart() + "_account_deposit_log like bq_account_deposit_log";
				String arbitrage_setting_sql = "create table if not exists " + o.getPart() + "_arbitrage_setting like bq_arbitrage_setting";
				String funding_fee_setting_sql = "create table if not exists " + o.getPart() + "_funding_fee_setting like bq_funding_fee_setting";
				String hedging_record_sql = "create table if not exists " + o.getPart() + "_hedging_record like bq_hedging_record";
				String last_ticker_sql = "create table if not exists " + o.getPart() + "_last_ticker like bq_last_ticker";
				String mark_price_setting_sql = "create table if not exists " + o.getPart() + "_mark_price_setting like bq_mark_price_setting";
				String member_transaction_sql = "create table if not exists " + o.getPart() + "_member_transaction like bq_member_transaction";
				String member_transaction_overload_log_sql = "create table if not exists " + o.getPart() + "_member_transaction_overload_log like bq_member_transaction_overload_log";
				String notradetime_sql = "create table if not exists " + o.getPart() + "_notradetime like bq_notradetime";
				String overload_match_setting_sql = "create table if not exists " + o.getPart() + "_overload_match_setting like bq_overload_match_setting";
				String statistical_form_sql = "create table if not exists " + o.getPart() + "_statistical_form like statistical_form";
				String statistical_hourly_form_sql = "create table if not exists " + o.getPart() + "_statistical_hourly_form like statistical_hourly_form";
				String tock_setting_sql = "create table if not exists " + o.getPart() + "_stock_setting like bq_stock_setting";
				String stop_loss_sql = "create table if not exists " + o.getPart() + "_stop_loss like bq_stop_loss";
				String trade_log_income_reconcilia_sql = "create table if not exists " + o.getPart() + "_trade_log_income_reconcilia like trade_log_income_reconcilia";
				String trade_log_reconcilia_sql = "create table if not exists " + o.getPart() + "_trade_log_reconcilia like trade_log_reconcilia";
				String transaction_sql = "create table if not exists " + o.getPart() + "_transaction like bq_transaction";
				String transaction_fee_rate_sql = "create table if not exists " + o.getPart() + "_transaction_fee_rate like bq_transaction_fee_rate";
				String transaction_partition_sql = "create table if not exists " + o.getPart() + "_transaction_partition like bq_transaction_partition";
				String transaction_rate_sql = "create table if not exists " + o.getPart() + "_transaction_rate like bq_transaction_rate";
				String transaction_type_sql = "create table if not exists " + o.getPart() + "_transaction_type like bq_transaction_type";
				
				String update_transaction_sql = "ALTER TABLE " + o.getPart() + "_transaction" +  
									" ADD COLUMN currency_type_id SMALLINT(6) NOT NULL DEFAULT 0 AFTER transaction_code";

				ps.addBatch(account_contract_sql);
				ps.addBatch(account_deposit_log_sql);
				ps.addBatch(arbitrage_setting_sql);
				ps.addBatch(funding_fee_setting_sql);
				ps.addBatch(hedging_record_sql);
				ps.addBatch(last_ticker_sql);
				ps.addBatch(mark_price_setting_sql);
				ps.addBatch(member_transaction_sql);
				ps.addBatch(member_transaction_overload_log_sql);
				ps.addBatch(notradetime_sql);
				ps.addBatch(overload_match_setting_sql);
				ps.addBatch(statistical_form_sql);
				ps.addBatch(statistical_hourly_form_sql);
				ps.addBatch(tock_setting_sql);
				ps.addBatch(stop_loss_sql);
				ps.addBatch(trade_log_income_reconcilia_sql);
				ps.addBatch(trade_log_reconcilia_sql);
				ps.addBatch(transaction_sql);
				ps.addBatch(transaction_fee_rate_sql);
				ps.addBatch(transaction_partition_sql);
				ps.addBatch(transaction_rate_sql);
				ps.addBatch(transaction_type_sql);
				ps.addBatch(update_transaction_sql);
				ps.executeBatch();
			}
		
		});
		
	}

	@Override
	public List<NContractPartition> readAll() {
		List<NContractPartition> partitions = spyMemcachedClient.get(BTCQMemecacheKeys.ALL_CONTRACT_PARTITION);
		if (partitions == null) {
			partitions = nContractPartitionDao.find("from NContractPartition");
			spyMemcachedClient.set(BTCQMemecacheKeys.ALL_CONTRACT_PARTITION, BTCQMemecacheKeys.TIMES.ONE_MIN, partitions);
		}
		
		return partitions;
	}
	
	@Override
	public List<NContractPartition> readUpdateAll() {
		List<NContractPartition> partitions = nContractPartitionDao.find("from NContractPartition");
		spyMemcachedClient.set(BTCQMemecacheKeys.ALL_CONTRACT_PARTITION, BTCQMemecacheKeys.TIMES.ONE_MIN, partitions);
		return partitions;
	}
	
	@Override
	public PagerModel readAllPages() {
		return nContractPartitionDao.searchHql("from NContractPartition");
	}

	@Override
	public NContractPartition getByPartition(String partition) {
		Map<String, Object> params = new HashMap<>();
		params.put("partition", partition);
		return nContractPartitionDao.get("from NContractPartition where part = :partition", params);
	}

	@Override
	public NContractPartition readByPartition(String partition) {
		List<NContractPartition> partitions = spyMemcachedClient.get(BTCQMemecacheKeys.ALL_CONTRACT_PARTITION);
		if (partitions != null) {
			for (NContractPartition part : partitions) {
				if (part.getPart().equals(partition)) {
					return part;
				}
			}
		}
		
		Map<String, Object> params = new HashMap<>();
		params.put("partition", partition);
		return nContractPartitionDao.get("from NContractPartition where part = :partition", params);
	}

	@Override
	public List<NContractPartition> findAll() {
		List<NContractPartition> partitions = spyMemcachedClient.get(BTCQMemecacheKeys.ALL_CONTRACT_PARTITION);
		if (partitions == null) {
			partitions = nContractPartitionDao.find("from NContractPartition");
			spyMemcachedClient.set(BTCQMemecacheKeys.ALL_CONTRACT_PARTITION, BTCQMemecacheKeys.TIMES.ONE_MIN, partitions);
		}
		
		return partitions;
	}

	
}
