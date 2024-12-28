package com.btc100.btc_q.service.impl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.VPendingLogDao;
import com.btc100.btc_q.entity.VPendingLog;
import com.btc100.btc_q.service.VPendingLogService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

@Service("vPendingLogService")
public class VPendingLogServiceImpl implements VPendingLogService {

	@Autowired
	private VPendingLogDao<VPendingLog> vPendingLogDao;

	public void savePendingLog(VPendingLog pendingLog) {
		vPendingLogDao.insertLog(pendingLog);
	}

	@Override
	public void createPendingLogBak(String code) {
		vPendingLogDao.createPendingLogBak(code);
	}

	@Override
	public void insertPendingLogBak(String code, long endTime) {
		vPendingLogDao.insertPendingLogBak(code, endTime);
		vPendingLogDao.deletePendingLog(code, endTime);
	}
	
	@Override
	public void updateActionByPendingNo(String bqCode, long pendingNo, long userId, int action) {
		vPendingLogDao.updateActionByPendingNo(bqCode, pendingNo, userId, action);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VPendingLog> readListByUserId(String code, long userId, long from, String direct, long startTime, long endTime, int limit) {
		Session session = vPendingLogDao.getSession();
		String sql = "select * from " + code + "_v_pending_log where f_user_id = :userId and trade_type_id < 5 and flag = 0";
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
		
		query.addEntity(VPendingLog.class);
		return query.list();
	}

	@Override
	public PagerModel readPagerMode(String code, long userId, int tradeType, long startTime, long endTime) {
		Session session = vPendingLogDao.getSession();
		
		String sql = "select * from " + code + "_v_pending_log WHERE f_user_id= :userId and trade_type_id < 5 and flag = 0";
		String countSql = "select count(1) from " + code + "_v_pending_log WHERE f_user_id= :userId and trade_type_id < 5 and flag = 0";
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

		query.addEntity(VPendingLog.class);
		
		PagerModel pm = new PagerModel();
		pm.setDatas(query.list());
		pm.setTotal(ConvertUtil.toInt(countQuery.uniqueResult(), 0));
		
		return pm;
	}
}
