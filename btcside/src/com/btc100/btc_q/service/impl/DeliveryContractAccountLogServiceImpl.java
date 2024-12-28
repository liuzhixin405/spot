package com.btc100.btc_q.service.impl;

import java.util.List;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.DeliveryContractAccountLogDao;
import com.btc100.btc_q.entity.DeliveryContractAccountLog;
import com.btc100.btc_q.service.DeliveryContractAccountLogService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

@Service("deliveryContractAccountLogService")
public class DeliveryContractAccountLogServiceImpl implements DeliveryContractAccountLogService {

	@Autowired
	private DeliveryContractAccountLogDao<DeliveryContractAccountLog> deliveryContractAccountLogDao;

	public PagerModel readPageAccount(long userId, String bqCode, int operation, long startTime, long endTime) {
		Session session = deliveryContractAccountLogDao.getSession();
		String wheresql = "where f_user_id = :userId and operation = :operation ";
		
		if (startTime > 0) {
			wheresql += " and dateline >= :startTime ";
		}
		
		if (endTime > 0) {
			wheresql += " and dateline < :endTime ";
		}
		wheresql += " ORDER BY dateline DESC ";
		
		StringBuffer sql = new StringBuffer("select * from " + bqCode + "_account_log ");
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
		query.addEntity(com.btc100.btc_q.entity.DeliveryContractAccountLog.class);
		
		StringBuilder countSql = new StringBuilder("select COUNT(log_id) from " + bqCode + "_account_log ");
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
	public void insertLog(DeliveryContractAccountLog log) {
		log.setDateline(deliveryContractAccountLogDao.getUnix_TIMESTAMP());
		deliveryContractAccountLogDao.insertLog(log);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DeliveryContractAccountLog> readByUserIdAndTime(String bqCode, int operation, long userId, long from, String direct,
			long startTime, long endTime, int limit) {
		Session session = deliveryContractAccountLogDao.getSession();
		StringBuffer sql = new StringBuffer("select * from " + bqCode + "_account_log where f_user_id = :userId");
		
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
		
		query.addEntity(DeliveryContractAccountLog.class);
		return query.list();
	}

	@Override
	public DeliveryContractAccountLog readByLogIdAndUserId(String bqCode, long logId, long userId) {
		Session session = deliveryContractAccountLogDao.getSession();
		StringBuffer sql = new StringBuffer("select * from " + bqCode + "_account_log where log_id = :logId and f_user_id = :userId");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("userId", userId);
		query.setParameter("logId", logId);
		query.addEntity(DeliveryContractAccountLog.class);
		
		return (DeliveryContractAccountLog) query.uniqueResult();
	}

	@Override
	public DeliveryContractAccountLog readByTransNoAndUserId(String bqCode, String transNo, long userId) {
		Session session = deliveryContractAccountLogDao.getSession();
		StringBuffer sql = new StringBuffer("select * from " + bqCode + "_account_log where trans_no = :transNo and f_user_id = :userId");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("userId", userId);
		query.setParameter("transNo", transNo);
		query.addEntity(DeliveryContractAccountLog.class);
		
		return (DeliveryContractAccountLog) query.uniqueResult();
	}

	@Override
	public void createAccountLogBak(String code) {
		deliveryContractAccountLogDao.createAccountLogBak(code);
	}

	@Override
	public void insertAccountLogBak(String code, long endTime) {
		deliveryContractAccountLogDao.insertAccountLogBak(code, endTime);
		deliveryContractAccountLogDao.deleteAccountLog(code, endTime);
	}

}
