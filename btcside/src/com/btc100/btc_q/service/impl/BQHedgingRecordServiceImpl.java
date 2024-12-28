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

import com.btc100.btc_q.dao.BQHedgingRecordDao;
import com.btc100.btc_q.entity.BQHedgingRecord;
import com.btc100.btc_q.service.BQHedgingRecordService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

@Service("bqHedgingRecordService")
public class BQHedgingRecordServiceImpl implements BQHedgingRecordService {

	@Autowired
	private BQHedgingRecordDao<BQHedgingRecord> bqHedgingRecordDao;

	@Override
	public BQHedgingRecord getRecordById(long id) {
		return bqHedgingRecordDao.get(BQHedgingRecord.class, id);
	}
	
	@Override
	public void save(BQHedgingRecord ba) {
		bqHedgingRecordDao.save(ba);
	}

	@Override
	public List<BQHedgingRecord> findRecordsByCodeAndState(String code, int state) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		params.put("code", code);
		return bqHedgingRecordDao.find("from BQHedgingRecord where state = :state and transaction_code = :code", params);
	}

	@Override
	public void updateState(long id, int state, BigDecimal price, String note) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		params.put("price", price);
		params.put("note", note);
		params.put("time", bqHedgingRecordDao.getUnix_TIMESTAMP());
		params.put("id", id);
		String hql = "update BQHedgingRecord set state = :state, price = :price, process_time = :time, note = :note where id = :id";
		bqHedgingRecordDao.updateCondition(hql, params);
	}

	@Override
	public void updateSendTimesAndNote(long id, BigDecimal price, String note) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("note", note);
		params.put("price", price);
		params.put("time", bqHedgingRecordDao.getUnix_TIMESTAMP());
		params.put("id", id);
		String hql = "update BQHedgingRecord set note = :note, price = :price, process_time = :time, send_times = send_times + 1 where id = :id";
		bqHedgingRecordDao.updateCondition(hql, params);
	}

	@Override
	public PagerModel readPagerModel(String transactionCode) {
		Session session = bqHedgingRecordDao.getSession();
		PagerModel pm = new PagerModel();
		
		String sql = "select * from bq_hedging_record where transaction_code=? ORDER BY id desc limit ?,? ";
		String sqlcount = "select count(1) from bq_hedging_record where transaction_code=? limit 1";
		SQLQuery querycount = session.createSQLQuery(sqlcount);
		querycount.setString(0, transactionCode);
		querycount.list().get(0);
		int total = ConvertUtil.toInt(querycount.uniqueResult(), 0);

		SQLQuery query = session.createSQLQuery(sql);
		query.setString(0, transactionCode);
		query.setInteger(1, SystemContext.getOffset());
		query.setInteger(2, SystemContext.getPageSize());

		query.addEntity(BQHedgingRecord.class);
		pm.setDatas(query.list());
		pm.setTotal(total);
		return pm;
	}

	@Override
	public void cancelRecord(BQHedgingRecord record, String note) {
		if (record.getState() != 0) {
				return;
			}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", 2);
		params.put("id", record.getId());
		params.put("note", note);
		params.put("state1", record.getState());
		params.put("processTime", bqHedgingRecordDao.getUnix_TIMESTAMP());
		bqHedgingRecordDao.updateCondition("update BQHedgingRecord set state = :state,process_time = :processTime,note = :note where id = :id and state = :state1", params);
	}

	@Override
	public BQHedgingRecord readCountFailedByTime(String code, long beginTime, long endTime, int direction) {
		Session session = bqHedgingRecordDao.getSession();
		
		StringBuilder sql = new StringBuilder("SELECT IFNULL(SUM(trade_amount), 0) trade_amount, IFNULL(SUM(trade_price * trade_amount)/SUM(trade_amount), 0) trade_price, IFNULL(transaction_code, '")
				.append(code).append("') transaction_code from bq_hedging_record WHERE process_time BETWEEN ")
				.append(beginTime).append(" AND ").append(endTime).append(" AND transaction_code = '" + code + "'");
		sql.append(" AND direction = " + direction + " AND state = 2");
		
		Query query = session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.aliasToBean(BQHedgingRecord.class));
		return (BQHedgingRecord) query.uniqueResult();
	}

	@Override
	public List<BQHedgingRecord> findRecordsByCodeAndStateBetweenTime(String code, int state, long beginTime, long endTime) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		params.put("code", code);
		params.put("beginTime", beginTime);
		params.put("endTime", endTime);
		return bqHedgingRecordDao.find("from BQHedgingRecord where state = :state and transaction_code = :code and process_time >= :beginTime and process_time < :endTime", params);
	}

}
