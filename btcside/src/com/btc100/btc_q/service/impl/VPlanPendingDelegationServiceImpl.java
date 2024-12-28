package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.VPlanPendingDelegationDao;
import com.btc100.btc_q.entity.VPlanPendingDelegation;
import com.btc100.btc_q.service.VPlanPendingDelegationService;
import com.btc100.btc_q.vo.PagerModel;

@Service("vPlanPendingDelegationService")
public class VPlanPendingDelegationServiceImpl implements VPlanPendingDelegationService {

	@Autowired
	private VPlanPendingDelegationDao<VPlanPendingDelegation> planPendingDelegationDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	@Override
	public VPlanPendingDelegation insert(VPlanPendingDelegation obj) throws HibernateException {
		VPlanPendingDelegation pd = planPendingDelegationDao.insert(obj);
		return pd;
	}

	@Override
	public List<VPlanPendingDelegation> findByTypeAndLastPrice(String code, int type, BigDecimal lastPrice, int limit) {
		return planPendingDelegationDao.findByTypeAndLastPrice(code, type, lastPrice, limit);
	}

	@Override
	public String lock(String code, long id) {
		return spyMemcachedClient.lock(BTCQMemecacheKeys.V_PLAN_PENDING_LOCK + code + "_" + id);
	}

	@Override
	public void unLock(String code, long id, String value) {
		spyMemcachedClient.unlock(BTCQMemecacheKeys.V_PLAN_PENDING_LOCK + code + "_" + id, value);
	}

	@Override
	public int updateStatusAndNote(String code, Long id, int status, String note) {
		return planPendingDelegationDao.updateStatusAndNote(code, id, status, note);
	}
	
	@Override
	public int deletePending(String code, Long id) throws HibernateException {
		return planPendingDelegationDao.deletePending(code, id);
	}

	@Override
	public List<VPlanPendingDelegation> readByUserId(String code, long fUserId, int direction, int status) {
		return planPendingDelegationDao.findByUserId(code, fUserId, direction, status);
	}

	@Override
	public List<VPlanPendingDelegation> readByUserIdDirection(int direction, String code, long fUserId) {
		return planPendingDelegationDao.findByUserIdDirection(direction, code, fUserId);
	}

	@Override
	public VPlanPendingDelegation readById(long id, String bqCode, long userId) {
		return planPendingDelegationDao.getById(id, bqCode, userId);
	}

	@Override
	public Long getUnix_TIMESTAMP() {
		return planPendingDelegationDao.getUnix_TIMESTAMP();
	}

	@Override
	public PagerModel getPagerModel(String code, Long uid, int direction, int status, int type) {
		return planPendingDelegationDao.getPagerModel(code, uid, direction, status, type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VPlanPendingDelegation> readPendingByUserIdAndTime(String code, long userId, long startTime,
			long endTime, long from, String direct, int size) {
		Session session = planPendingDelegationDao.getSession();
		String sql = "select * from " + code + "_v_plan_pending_delegation where f_user_id = :userId";
		if (startTime > 0) {
			sql += " and delegation_time >= :startTime";
		}
		
		if (endTime > 0) {
			sql += " and delegation_time <= :endTime";
		}
		
		if (from >= 0 && "prev".equals(direct)) {
			sql += " and id >= :from";
		} else if (from >= 0 && "next".equals(direct)) {
			sql += " and id < :from";
		}
		
		sql += " order by id desc";
		
		if (size > 0) {
			sql += " limit " + size;
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
		
		query.addEntity(VPlanPendingDelegation.class);
		
		return query.list();
	}

	@Override
	public VPlanPendingDelegation readOneByStatus(String code, long userId, int status, int multiple) {
		Session session = planPendingDelegationDao.getSession();
		String sql = "select * from " + code + "_v_plan_pending_delegation where f_user_id = :userId";
		sql += " and status = :status and multiple = :multiple limit 0,1 ";
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("userId", userId);
		query.setInteger("status", status);
		query.setParameter("multiple", multiple);
		query.addEntity(VPlanPendingDelegation.class);
		return (VPlanPendingDelegation) query.uniqueResult();
	}

}
