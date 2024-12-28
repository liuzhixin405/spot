package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQSpotHedgingRecordDao;
import com.btc100.btc_q.entity.BQSpotHedgingRecord;
import com.btc100.btc_q.service.BQSpotHedgingRecordService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

@Service("bqSpotHedgingRecordService")
public class BQSpotHedgingRecordServiceImpl implements BQSpotHedgingRecordService {

	@Autowired
	private BQSpotHedgingRecordDao<BQSpotHedgingRecord> bqSpotHedgingRecordDao;

	@Override
	public BQSpotHedgingRecord getRecordById(long id) {
		return bqSpotHedgingRecordDao.get(BQSpotHedgingRecord.class, id);
	}
	
	@Override
	public void save(BQSpotHedgingRecord ba) {
		bqSpotHedgingRecordDao.save(ba);
	}

	@Override
	public List<BQSpotHedgingRecord> findRecordsByCodeAndState(String code, int state) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		params.put("code", code);
		return bqSpotHedgingRecordDao.find("from BQSpotHedgingRecord where state = :state and transaction_code = :code", params);
	}

	@Override
	public void updateState(long id, int state, BigDecimal price, String note) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		params.put("price", price);
		params.put("note", note);
		params.put("time", bqSpotHedgingRecordDao.getUnix_TIMESTAMP());
		params.put("id", id);
		String hql = "update BQSpotHedgingRecord set state = :state, price = :price, process_time = :time, note = :note where id = :id";
		bqSpotHedgingRecordDao.updateCondition(hql, params);
	}

	@Override
	public void updateSendTimesAndNote(long id, BigDecimal price, String note) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("note", note);
		params.put("price", price);
		params.put("time", bqSpotHedgingRecordDao.getUnix_TIMESTAMP());
		params.put("id", id);
		String hql = "update BQSpotHedgingRecord set note = :note, price = :price, process_time = :time, send_times = send_times + 1 where id = :id";
		bqSpotHedgingRecordDao.updateCondition(hql, params);
	}

	@Override
	public PagerModel readModel(String transactionCode) {
		Session session = bqSpotHedgingRecordDao.getSession();
		PagerModel pm = new PagerModel();
		
		String sql = "select * from bq_spot_hedging_record where transaction_code=? ORDER BY id desc limit ?,? ";
		String sqlcount = "select count(1) from bq_spot_hedging_record where transaction_code=? limit 1";
		SQLQuery querycount = session.createSQLQuery(sqlcount);
		querycount.setString(0, transactionCode);
		querycount.list().get(0);
		int total = ConvertUtil.toInt(querycount.uniqueResult(), 0);

		SQLQuery query = session.createSQLQuery(sql);
		query.setString(0, transactionCode);
		query.setInteger(1, SystemContext.getOffset());
		query.setInteger(2, SystemContext.getPageSize());

		query.addEntity(BQSpotHedgingRecord.class);
		pm.setDatas(query.list());
		pm.setTotal(total);
		return pm;
	}

	@Override
	public void cancelRecord(BQSpotHedgingRecord record, String note) {
		if (record.getState() != 0) {
				return;
			}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", 2);
		params.put("id", record.getId());
		params.put("note", note);
		params.put("state1", record.getState());
		params.put("processTime", bqSpotHedgingRecordDao.getUnix_TIMESTAMP());
		bqSpotHedgingRecordDao.updateCondition("update BQSpotHedgingRecord set state = :state,process_time = :processTime,note = :note where id = :id and state = :state1", params);
	}

	@Override
	public BQSpotHedgingRecord readCountFailedByTime(String code, long beginTime, long endTime, int direction) {
		Session session = bqSpotHedgingRecordDao.getSession();
		
		StringBuilder sql = new StringBuilder("SELECT IFNULL(SUM(trade_amount), 0) trade_amount, IFNULL(SUM(trade_price * trade_amount)/SUM(trade_amount), 0) trade_price, IFNULL(transaction_code, '")
				.append(code).append("') transaction_code from bq_spot_hedging_record WHERE process_time BETWEEN ")
				.append(beginTime).append(" AND ").append(endTime).append(" AND transaction_code = '" + code + "'");
		sql.append(" AND direction = " + direction + " AND state = 2");
		
		Query query = session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.aliasToBean(BQSpotHedgingRecord.class));
		return (BQSpotHedgingRecord) query.uniqueResult();
	}

	@Override
	public List<BQSpotHedgingRecord> findRecordsByCodeAndStateBetweenTime(String code, int state, long beginTime, long endTime) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		params.put("code", code);
		params.put("beginTime", beginTime);
		params.put("endTime", endTime);
		return bqSpotHedgingRecordDao.find("from BQSpotHedgingRecord where state = :state and transaction_code = :code and process_time >= :beginTime and process_time < :endTime", params);
	}

}
