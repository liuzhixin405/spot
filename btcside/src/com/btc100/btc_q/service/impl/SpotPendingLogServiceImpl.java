package com.btc100.btc_q.service.impl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.SpotPendingLogDao;
import com.btc100.btc_q.entity.SpotPendingLog;
import com.btc100.btc_q.service.SpotPendingLogService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

@Service("spotPendingLogService")
public class SpotPendingLogServiceImpl implements SpotPendingLogService {

	@Autowired
	private SpotPendingLogDao<SpotPendingLog> pendingLogDao;

	@SuppressWarnings("unchecked")
	@Override
	public List<SpotPendingLog> readByPendingNo(String code, String pendingNo, long userId) {
		Session session = pendingLogDao.getSession();
		String sql = "select * from " + code + "_pending_log where pending_no = :pendingNo and f_user_id = :userId order by dateline desc limit 1";
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setString("pendingNo", pendingNo);
		query.setLong("userId", userId);
		
		query.addEntity(SpotPendingLog.class);
		return query.list();
	}

	@Override
	public void deleteByUidBeforeTime(String code, long userId, long time, int action) {
		Session session = pendingLogDao.getSession();
		String sql = "delete from " + code + "_pending_log where f_user_id = :userId and dateline < :time and action = :action and flag = 0";
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("userId", userId);
		query.setLong("time", time);
		query.setLong("action", action);
		query.executeUpdate();
	}

	@Override
	public void createPendingLogBak(String code) {
		pendingLogDao.createPendingLogBak(code);
	}

	@Override
	public void insertPendingLogBak(String code, long endTime) {
		pendingLogDao.insertPendingLogBak(code, endTime);
		pendingLogDao.deletePendingLog(code, endTime);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpotPendingLog> readListByUserId(String code, long userId, long from, String direct, long startTime, long endTime, int limit) {
		Session session = pendingLogDao.getSession();
		String sql = "select * from " + code + "_pending_log where f_user_id = :userId and flag = 0";
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
		
		query.addEntity(SpotPendingLog.class);
		return query.list();
	}

	@Override
	public PagerModel readPagerMode(String code, long userId, int tradeType, long startTime, long endTime) {
		Session session = pendingLogDao.getSession();
		
		String sql = "select * from " + code + "_pending_log WHERE f_user_id= :userId and flag = 0";
		String countSql = "select count(1) from " + code + "_pending_log WHERE f_user_id= :userId and flag = 0";
		if (tradeType != 0) {
			sql += " and direction = :tradeTypeId";
			countSql += " and direction = :tradeTypeId";
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

		query.addEntity(SpotPendingLog.class);
		
		PagerModel pm = new PagerModel();
		pm.setDatas(query.list());
		pm.setTotal(ConvertUtil.toInt(countQuery.uniqueResult(), 0));
		
		return pm;
	}
}
