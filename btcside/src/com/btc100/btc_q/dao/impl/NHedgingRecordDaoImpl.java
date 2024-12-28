package com.btc100.btc_q.dao.impl;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.NHedgingRecordDao;
import com.btc100.btc_q.entity.NHedgingRecord;

@Repository("nHedgingRecordDao")
public class NHedgingRecordDaoImpl extends BaseDaoImpl<NHedgingRecord> implements NHedgingRecordDao<NHedgingRecord> {

	@Override
	public int insert(String partition, NHedgingRecord record) {
		Session session = getSession();
		String sql = "insert into " + partition + "_hedging_record "
				+ "(transaction_code,trade_id,trade_amount,trade_price,trade_direction,trade_time,f_user_id,direction,price,process_time,state,note,send_times) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";

		SQLQuery query = session.createSQLQuery(sql);

		query.setString(0, record.getTransaction_code());
		query.setLong(1, record.getTrade_id());
		query.setBigDecimal(2, record.getTrade_amount());
		query.setBigDecimal(3, record.getTrade_price());
		query.setInteger(4, record.getTrade_direction());
		query.setLong(5, record.getTrade_time());
		query.setLong(6, record.getF_user_id());
		query.setInteger(7, record.getDirection());
		query.setBigDecimal(8, record.getPrice());
		query.setLong(9, record.getProcess_time());
		query.setInteger(10, record.getState());
		query.setString(11, record.getNote());
		query.setInteger(12, record.getSend_times());
		
		return query.executeUpdate();
	}
	
}
