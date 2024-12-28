package com.btc100.btc_q.service.impl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.NPendingLogDao;
import com.btc100.btc_q.entity.NPendingLog;
import com.btc100.btc_q.service.NPendingLogService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

@Service("nNPendingLogService")
public class NPendingLogServiceImpl implements NPendingLogService {

	@Autowired
	private NPendingLogDao<NPendingLog> pendingLogDao;

	public void savePendingLog(String partition, NPendingLog pendingLog) {
		pendingLogDao.insertLog(partition, pendingLog);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NPendingLog> findByPendingNo(String partition, String code, String pendingNo, long userId) {
		Session session = pendingLogDao.getSession();
		String sql = "select * from " + partition + "_" + code + "_pending_log where pending_no = :pendingNo and f_user_id = :userId order by dateline desc limit 1";
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setString("pendingNo", pendingNo);
		query.setLong("userId", userId);
		
		query.addEntity(NPendingLog.class);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<NPendingLog> readByPendingNo(String partition, String code, String pendingNo, long userId) {
		Session session = pendingLogDao.getSession();
		String sql = "select * from " + partition + "_" + code + "_pending_log where pending_no = :pendingNo and f_user_id = :userId order by dateline desc limit 1";
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setString("pendingNo", pendingNo);
		query.setLong("userId", userId);
		
		query.addEntity(NPendingLog.class);
		return query.list();
	}

	@Override
	public void createPendingLogBak(String partition, String code) {
		pendingLogDao.createPendingLogBak(partition, code);
	}

	@Override
	public void insertPendingLogBak(String partition, String code, long endTime) {
		pendingLogDao.insertPendingLogBak(partition, code, endTime);
		pendingLogDao.deletePendingLog(partition, code, endTime);
	}

	@Override
	public void updateActionByPendingNo(String partition, String bqCode, long pendingNo, long userId, int action) {
		pendingLogDao.updateActionByPendingNo(partition, bqCode, pendingNo, userId, action);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NPendingLog> readListByUserId(String partition, String code, long userId, long from, String direct, long startTime, long endTime, int limit) {
		Session session = pendingLogDao.getSession();
		String sql = "select * from " + partition + "_" + code + "_pending_log where f_user_id = :userId and trade_type_id < 5 and flag = 0";
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
		
		query.addEntity(NPendingLog.class);
		return query.list();
	}

	@Override
	public PagerModel readPagerMode(String partition, String code, long userId, int tradeType, long startTime, long endTime) {
		Session session = pendingLogDao.getSession();
		
		String sql = "select * from " + partition + "_" + code + "_pending_log WHERE f_user_id= :userId and trade_type_id < 5 and flag = 0";
		String countSql = "select count(1) from " + partition + "_" + code + "_pending_log WHERE f_user_id= :userId and trade_type_id < 5 and flag = 0";
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

		query.addEntity(NPendingLog.class);
		
		PagerModel pm = new PagerModel();
		pm.setDatas(query.list());
		pm.setTotal(ConvertUtil.toInt(countQuery.uniqueResult(), 0));
		
		return pm;
	}

	@Override
	public void deleteByUidBeforeTime(String partition, String code, long userId, long time, int action) {
		Session session = pendingLogDao.getSession();
		String sql = "delete from " + partition + "_" + code + "_pending_log where f_user_id = :userId and dateline < :time and action = :action and flag = 0";
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("userId", userId);
		query.setLong("time", time);
		query.setLong("action", action);
		query.executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<NPendingLog> readPendingByUserIdAndSymbol(String partition, String code, long userId, String orderIds) {
		Session session = pendingLogDao.getSession();
		String sql = "select * from " + partition + "_" + code + "_pending_log where f_user_id = :userId ";
		sql += " and pending_no in (" + orderIds + ")";
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("userId", userId);
		query.addEntity(NPendingLog.class);
		
		return query.list();
	}
}
