package com.btc100.btc_q.service.impl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.DeliveryPendingLogDao;
import com.btc100.btc_q.entity.DeliveryPendingLog;
import com.btc100.btc_q.service.DeliveryPendingLogService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

@Service("DeliveryPendingLogService")
public class DeliveryPendingLogServiceImpl implements DeliveryPendingLogService {

	@Autowired
	private DeliveryPendingLogDao<DeliveryPendingLog> deliveryPendingLogDao;

	public void savePendingLog(DeliveryPendingLog DeliveryPendingLog) {
		deliveryPendingLogDao.insertLog(DeliveryPendingLog);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DeliveryPendingLog> findByPendingNo(String code, String pendingNo, long userId) {
		Session session = deliveryPendingLogDao.getSession();
		String sql = "select * from " + code + "_pending_log where pending_no = :pendingNo and f_user_id = :userId order by dateline desc limit 1";
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setString("pendingNo", pendingNo);
		query.setLong("userId", userId);
		
		query.addEntity(DeliveryPendingLog.class);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<DeliveryPendingLog> readByPendingNo(String code, String pendingNo, long userId) {
		Session session = deliveryPendingLogDao.getSession();
		String sql = "select * from " + code + "_pending_log where pending_no = :pendingNo and f_user_id = :userId order by dateline desc limit 1";
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setString("pendingNo", pendingNo);
		query.setLong("userId", userId);
		
		query.addEntity(DeliveryPendingLog.class);
		return query.list();
	}

	@Override
	public void createDeliveryPendingLogBak(String code) {
		deliveryPendingLogDao.createDeliveryPendingLogBak(code);
	}

	@Override
	public void insertDeliveryPendingLogBak(String code, long endTime) {
		deliveryPendingLogDao.insertDeliveryPendingLogBak(code, endTime);
		deliveryPendingLogDao.deleteDeliveryPendingLog(code, endTime);
	}

	@Override
	public void updateActionByPendingNo(String bqCode, long pendingNo, long userId, int action) {
		deliveryPendingLogDao.updateActionByPendingNo(bqCode, pendingNo, userId, action);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DeliveryPendingLog> readListByUserId(String code, long userId, long from, String direct, long startTime, long endTime, int limit) {
		Session session = deliveryPendingLogDao.getSession();
		String sql = "select * from " + code + "_pending_log where f_user_id = :userId and trade_type_id < 5 and flag = 0";
		if (startTime > 0) {
			sql += " and delegation_time >= :startTime";
		}
		
		if (endTime > 0) {
			sql += " and delegation_time < :endTime";
		}
		
		if (from >= 0 && "prev".equals(direct)) {
			sql += " and log_id " + ">=" + " :from";
		} else if (from >= 0 && "next".equals(direct)) {
			sql += " and log_id " + "<" + " :from";
		}
		
		sql +=  " order by log_id desc";
		
		if (limit > 0) {
			sql += " limit " + limit;	
		}
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("userId", userId);
		
		if (startTime > 0) {
			query.setLong("startTime", startTime);
		}
		
		if (endTime > 0) {
			query.setLong("endTime", endTime);
		}
		
		if (from >= 0 && "prev".equals(direct)) {
			query.setLong("from", from);
		} else if (from >= 0 && "next".equals(direct)) {
			query.setLong("from", from);
		}
		
		query.addEntity(DeliveryPendingLog.class);
		return query.list();
	}

	@Override
	public PagerModel readPagerMode(String code, long userId, int tradeType, long startTime, long endTime) {
		Session session = deliveryPendingLogDao.getSession();
		
		String sql = "select * from " + code + "_pending_log WHERE f_user_id= :userId and trade_type_id < 5 and flag = 0";
		String countSql = "select count(1) from " + code + "_pending_log WHERE f_user_id= :userId and trade_type_id < 5 and flag = 0";
		if (tradeType != 0) {
			sql += " and trade_type_id= :tradeTypeId";
			countSql += " and trade_type_id= :tradeTypeId";
		}
		
		if (startTime > 0) {
			sql += " and delegation_time >= :startTime";
			countSql += " and delegation_time >= :startTime";
		}
		
		if (endTime > 0) {
			sql += " and delegation_time < :endTime";
			countSql += " and delegation_time < :endTime";
		}
		
		sql += " ORDER BY log_id DESC LIMIT " + SystemContext.getOffset() + "," + SystemContext.getPageSize();
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("userId", userId);
		
		SQLQuery countQuery = session.createSQLQuery(countSql);
		countQuery.setParameter("userId", userId);
		
		if (tradeType > 0) {
			query.setParameter("tradeTypeId", tradeType);
			countQuery.setParameter("tradeTypeId", tradeType);
		}
		
		if (startTime > 0) {
			query.setParameter("startTime", startTime);
			countQuery.setParameter("startTime", startTime);
		}
		
		if (endTime > 0) {
			query.setParameter("endTime", endTime);
			countQuery.setParameter("endTime", endTime);
		}

		query.addEntity(DeliveryPendingLog.class);
		
		PagerModel pm = new PagerModel();
		pm.setDatas(query.list());
		pm.setTotal(ConvertUtil.toInt(countQuery.uniqueResult(), 0));
		
		return pm;
	}
}
