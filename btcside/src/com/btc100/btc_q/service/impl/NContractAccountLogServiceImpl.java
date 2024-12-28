package com.btc100.btc_q.service.impl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.NContractAccountLogDao;
import com.btc100.btc_q.entity.NContractAccountLog;
import com.btc100.btc_q.service.NContractAccountLogService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

@Service("nContractAccountLogService")
public class NContractAccountLogServiceImpl implements NContractAccountLogService {

	@Autowired
	private NContractAccountLogDao<NContractAccountLog> nContractAccountLogDao;

	public PagerModel readPageAccount(String partition, long userId, String vCode, int operation, long startTime, long endTime) {
		Session session = nContractAccountLogDao.getSession();
		String wheresql = "where f_user_id = :userId and operation = :operation ";
		
		if (startTime > 0) {
			wheresql += " and dateline >= :startTime ";
		}
		
		if (endTime > 0) {
			wheresql += " and dateline < :endTime ";
		}
		wheresql += " ORDER BY dateline DESC ";
		
		StringBuffer sql = new StringBuffer("select * from " + partition + "_" + vCode + "_account_log ");
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
		query.addEntity(com.btc100.btc_q.entity.NContractAccountLog.class);
		
		StringBuilder countSql = new StringBuilder("select COUNT(log_id) from " + partition + "_" + vCode + "_account_log ");
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
	public void insertLog(String partition, NContractAccountLog log) {
		log.setDateline(nContractAccountLogDao.getUnix_TIMESTAMP());
		nContractAccountLogDao.insertLog(partition, log);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NContractAccountLog> readByUserIdAndTime(String partition, String vCode, int operation, long userId, long from,
			String direct, long startTime, long endTime, int limit) {
		Session session = nContractAccountLogDao.getSession();
		StringBuffer sql = new StringBuffer("select * from " + partition + "_" + vCode + "_account_log where f_user_id = :userId");
		
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
		
		query.addEntity(NContractAccountLog.class);
		return query.list();
	}

	@Override
	public NContractAccountLog readByLogIdAndUserId(String partition, String vCode, long logId, long userId) {
		Session session = nContractAccountLogDao.getSession();
		StringBuffer sql = new StringBuffer("select * from " + partition + "_" + vCode + "_account_log where log_id = :logId and f_user_id = :userId");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("userId", userId);
		query.setParameter("logId", logId);
		query.addEntity(NContractAccountLog.class);
		
		return (NContractAccountLog) query.uniqueResult();
	}

	@Override
	public NContractAccountLog readByTransNoAndUserId(String partition, String vCode, String transNo, long userId) {
		Session session = nContractAccountLogDao.getSession();
		StringBuffer sql = new StringBuffer("select * from " + partition + "_" + vCode + "_account_log where trans_no = :transNo and f_user_id = :userId");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("userId", userId);
		query.setParameter("transNo", transNo);
		query.addEntity(NContractAccountLog.class);
		
		return (NContractAccountLog) query.uniqueResult();
	}

	@Override
	public void createAccountLogBak(String partition, String code) {
		nContractAccountLogDao.createAccountLogBak(partition, code);
	}

	@Override
	public void insertAccountLogBak(String partition, String code, long endTime) {
		nContractAccountLogDao.insertAccountLogBak(partition, code, endTime);
		nContractAccountLogDao.deleteAccountLog(partition, code, endTime);
	}

}
