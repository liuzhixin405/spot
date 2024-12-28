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

import com.btc100.btc_q.dao.DeliveryHedgingRecordDao;
import com.btc100.btc_q.entity.DeliveryHedgingRecord;
import com.btc100.btc_q.service.DeliveryHedgingRecordService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

@Service("deliveryHedgingRecordService")
public class DeliveryHedgingRecordServiceImpl implements DeliveryHedgingRecordService {

	@Autowired
	private DeliveryHedgingRecordDao<DeliveryHedgingRecord> deliveryHedgingRecordDao;

	@Override
	public DeliveryHedgingRecord getRecordById(long id) {
		return deliveryHedgingRecordDao.get(DeliveryHedgingRecord.class, id);
	}
	
	@Override
	public void save(DeliveryHedgingRecord ba) {
		deliveryHedgingRecordDao.save(ba);
	}

	@Override
	public List<DeliveryHedgingRecord> findRecordsByCodeAndState(String code, int state) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		params.put("code", code);
		return deliveryHedgingRecordDao.find("from DeliveryHedgingRecord where state = :state and transaction_code = :code", params);
	}

	@Override
	public void updateState(long id, int state, BigDecimal price, String note) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		params.put("price", price);
		params.put("note", note);
		params.put("time", deliveryHedgingRecordDao.getUnix_TIMESTAMP());
		params.put("id", id);
		String hql = "update DeliveryHedgingRecord set state = :state, price = :price, process_time = :time, note = :note where id = :id";
		deliveryHedgingRecordDao.updateCondition(hql, params);
	}

	@Override
	public void updateSendTimesAndNote(long id, BigDecimal price, String note) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("note", note);
		params.put("price", price);
		params.put("time", deliveryHedgingRecordDao.getUnix_TIMESTAMP());
		params.put("id", id);
		String hql = "update DeliveryHedgingRecord set note = :note, price = :price, process_time = :time, send_times = send_times + 1 where id = :id";
		deliveryHedgingRecordDao.updateCondition(hql, params);
	}

	@Override
	public PagerModel readPagerModel(String transactionCode) {
		Session session = deliveryHedgingRecordDao.getSession();
		PagerModel pm = new PagerModel();
		
		String sql = "select * from delivery_hedging_record where transaction_code=? ORDER BY id desc limit ?,? ";
		String sqlcount = "select count(1) from delivery_hedging_record where transaction_code=? limit 1";
		SQLQuery querycount = session.createSQLQuery(sqlcount);
		querycount.setString(0, transactionCode);
		querycount.list().get(0);
		int total = ConvertUtil.toInt(querycount.uniqueResult(), 0);

		SQLQuery query = session.createSQLQuery(sql);
		query.setString(0, transactionCode);
		query.setInteger(1, SystemContext.getOffset());
		query.setInteger(2, SystemContext.getPageSize());

		query.addEntity(DeliveryHedgingRecord.class);
		pm.setDatas(query.list());
		pm.setTotal(total);
		return pm;
	}

	@Override
	public void cancelRecord(DeliveryHedgingRecord record, String note) {
		if (record.getState() != 0) {
				return;
			}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", 2);
		params.put("id", record.getId());
		params.put("note", note);
		params.put("state1", record.getState());
		params.put("processTime", deliveryHedgingRecordDao.getUnix_TIMESTAMP());
		deliveryHedgingRecordDao.updateCondition("update DeliveryHedgingRecord set state = :state,process_time = :processTime,note = :note where id = :id and state = :state1", params);
	}

	@Override
	public DeliveryHedgingRecord readCountFailedByTime(String code, long beginTime, long endTime, int direction) {
		Session session = deliveryHedgingRecordDao.getSession();
		
		StringBuilder sql = new StringBuilder("SELECT IFNULL(SUM(trade_amount), 0) trade_amount, IFNULL(SUM(trade_price * trade_amount)/SUM(trade_amount), 0) trade_price, IFNULL(transaction_code, '")
				.append(code).append("') transaction_code from delivery_hedging_record WHERE process_time BETWEEN ")
				.append(beginTime).append(" AND ").append(endTime).append(" AND transaction_code = '" + code + "'");
		sql.append(" AND direction = " + direction + " AND state = 2");
		
		Query query = session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.aliasToBean(DeliveryHedgingRecord.class));
		return (DeliveryHedgingRecord) query.uniqueResult();
	}

	@Override
	public List<DeliveryHedgingRecord> findRecordsByCodeAndStateBetweenTime(String code, int state, long beginTime, long endTime) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		params.put("code", code);
		params.put("beginTime", beginTime);
		params.put("endTime", endTime);
		return deliveryHedgingRecordDao.find("from DeliveryHedgingRecord where state = :state and transaction_code = :code and process_time >= :beginTime and process_time < :endTime", params);
	}

}
