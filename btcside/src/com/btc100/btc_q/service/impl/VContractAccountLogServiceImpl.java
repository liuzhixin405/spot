package com.btc100.btc_q.service.impl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.VContractAccountLogDao;
import com.btc100.btc_q.entity.VContractAccountLog;
import com.btc100.btc_q.service.VContractAccountLogService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

@Service("vContractAccountLogService")
public class VContractAccountLogServiceImpl implements VContractAccountLogService {

	@Autowired
	private VContractAccountLogDao<VContractAccountLog> vContractAccountLogDao;

	public PagerModel readPageAccount(long userId, String vCode, int operation, long startTime, long endTime) {
		Session session = vContractAccountLogDao.getSession();
		String wheresql = "where f_user_id = :userId and operation = :operation ";
		
		if (startTime > 0) {
			wheresql += " and dateline >= :startTime ";
		}
		
		if (endTime > 0) {
			wheresql += " and dateline < :endTime ";
		}
		wheresql += " ORDER BY dateline DESC ";
		
		StringBuffer sql = new StringBuffer("select * from " + vCode + "_v_account_log ");
		sql.append(wheresql).append("LIMIT ").append(SystemContext.getOffset()).append(", ").append(SystemContext.getPageSize());
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("userId", userId);
		query.setParameter("operation", operation);
		if (startTime > 0) {
			query.setParameter("startTime", startTime);
		}
		if (endTime > 0) {
			query.setParameter("endTime", endTime);
		}
		query.addEntity(com.btc100.btc_q.entity.VContractAccountLog.class);
		
		StringBuilder countSql = new StringBuilder("select COUNT(log_id) from " + vCode + "_v_account_log ");
		countSql.append(wheresql);
		
		SQLQuery countQuery = session.createSQLQuery(countSql.toString());
		countQuery.setParameter("userId", userId);
		countQuery.setParameter("operation", operation);
		if (startTime > 0) {
			countQuery.setParameter("startTime", startTime);
		}
		if (endTime > 0) {
			countQuery.setParameter("endTime", endTime);
		}
		
		PagerModel pm = new PagerModel();
		pm.setDatas(query.list());
		pm.setTotal(ConvertUtil.toInt(countQuery.uniqueResult(), 0));
		return pm;
	}
	
	@Override
	public void insertLog(VContractAccountLog log) {
		log.setDateline(vContractAccountLogDao.getUnix_TIMESTAMP());
		vContractAccountLogDao.insertLog(log);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VContractAccountLog> readByUserIdAndTime(String vCode, int operation, long userId, long from,
			String direct, long startTime, long endTime, int limit) {
		Session session = vContractAccountLogDao.getSession();
		StringBuffer sql = new StringBuffer("select * from " + vCode + "_v_account_log where f_user_id = :userId");
		
		if (operation == 0) {
			sql.append(" and operation > :operation and operation <= 2");
		}
		
		if (operation > 0) {
			sql.append(" and operation = :operation ");
		}
		
		if (startTime > 0) {
			sql.append(" and dateline >= :startTime");
		}
		
		if (endTime > 0) {
			sql.append(" and dateline <= :endTime");
		}
		
		if (from >= 0 && "prev".equals(direct)) {
			sql.append(" and log_id " + ">=" + " :from");
		} else if (from >= 0 && "next".equals(direct)) {
			sql.append(" and log_id " + "<" + " :from");
		}
		
		sql.append(" ORDER BY log_id DESC");
		
		if (limit > 0) {
			sql.append(" limit " + limit);	
		}
		
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setLong("userId", userId);
		
		if (operation >= 0) {
			query.setLong("operation", operation);
		}
		
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
		
		query.addEntity(VContractAccountLog.class);
		return query.list();
	}

	@Override
	public VContractAccountLog readByLogIdAndUserId(String vCode, long logId, long userId) {
		Session session = vContractAccountLogDao.getSession();
		StringBuffer sql = new StringBuffer("select * from " + vCode + "_v_account_log where log_id = :logId and f_user_id = :userId");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("userId", userId);
		query.setParameter("logId", logId);
		query.addEntity(VContractAccountLog.class);
		
		return (VContractAccountLog) query.uniqueResult();
	}

	@Override
	public VContractAccountLog readByTransNoAndUserId(String vCode, String transNo, long userId) {
		Session session = vContractAccountLogDao.getSession();
		StringBuffer sql = new StringBuffer("select * from " + vCode + "_v_account_log where trans_no = :transNo and f_user_id = :userId");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("userId", userId);
		query.setParameter("transNo", transNo);
		query.addEntity(VContractAccountLog.class);
		
		return (VContractAccountLog) query.uniqueResult();
	}

	@Override
	public void createAccountLogBak(String code) {
		vContractAccountLogDao.createAccountLogBak(code);
	}

	@Override
	public void insertAccountLogBak(String code, long endTime) {
		vContractAccountLogDao.insertAccountLogBak(code, endTime);
		vContractAccountLogDao.deleteAccountLog(code, endTime);
	}

}
