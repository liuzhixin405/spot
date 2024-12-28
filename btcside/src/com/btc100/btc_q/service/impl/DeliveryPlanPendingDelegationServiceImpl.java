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
import com.btc100.btc_q.dao.DeliveryPlanPendingDelegationDao;
import com.btc100.btc_q.entity.DeliveryPlanPendingDelegation;
import com.btc100.btc_q.service.DeliveryPlanPendingDelegationService;
import com.btc100.btc_q.vo.PagerModel;

@Service("DeliveryPlanPendingDelegationService")
public class DeliveryPlanPendingDelegationServiceImpl implements DeliveryPlanPendingDelegationService {

	@Autowired
	private DeliveryPlanPendingDelegationDao<DeliveryPlanPendingDelegation> deliveryPlanPendingDelegationDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	@Override
	public DeliveryPlanPendingDelegation insert(DeliveryPlanPendingDelegation obj) throws HibernateException {
		DeliveryPlanPendingDelegation pd = deliveryPlanPendingDelegationDao.insert(obj);
		return pd;
	}

	@Override
	public List<DeliveryPlanPendingDelegation> findByTypeAndLastPrice(String code, int type, BigDecimal lastPrice, int limit) {
		return deliveryPlanPendingDelegationDao.findByTypeAndLastPrice(code, type, lastPrice, limit);
	}

	@Override
	public String lock(String code, long id) {
		return spyMemcachedClient.lock(BTCQMemecacheKeys.PLAN_PENDING_LOCK + code + "_" + id);
	}

	@Override
	public void unLock(String code, long id, String value) {
		spyMemcachedClient.unlock(BTCQMemecacheKeys.PLAN_PENDING_LOCK + code + "_" + id, value);
	}

	@Override
	public int updateStatusAndNote(String code, Long id, int status, String note) {
		return deliveryPlanPendingDelegationDao.updateStatusAndNote(code, id, status, note);
	}
	
	@Override
	public int deletePending(String code, Long id) throws HibernateException {
		return deliveryPlanPendingDelegationDao.deletePending(code, id);
	}

	@Override
	public List<DeliveryPlanPendingDelegation> readByUserId(String code, long fUserId, int direction, int status) {
		return deliveryPlanPendingDelegationDao.findByUserId(code, fUserId, direction, status);
	}

	@Override
	public List<DeliveryPlanPendingDelegation> readByUserIdDirection(int direction, String code, long fUserId) {
		return deliveryPlanPendingDelegationDao.findByUserIdDirection(direction, code, fUserId);
	}

	@Override
	public DeliveryPlanPendingDelegation readById(long id, String bqCode, long userId) {
		return deliveryPlanPendingDelegationDao.getById(id, bqCode, userId);
	}

	@Override
	public Long getUnix_TIMESTAMP() {
		return deliveryPlanPendingDelegationDao.getUnix_TIMESTAMP();
	}

	@Override
	public PagerModel getPagerModel(String code, Long uid, int direction, int status, int type) {
		return deliveryPlanPendingDelegationDao.getPagerModel(code, uid, direction, status, type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DeliveryPlanPendingDelegation> readPendingByUserIdAndTime(String code, long userId, long startTime,
			long endTime, long from, String direct, int size) {
		Session session = deliveryPlanPendingDelegationDao.getSession();
		String sql = "select * from " + code + "_plan_pending_delegation where f_user_id = :userId";
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
		
		query.addEntity(DeliveryPlanPendingDelegation.class);
		
		return query.list();
	}

	@Override
	public DeliveryPlanPendingDelegation readOneByStatus(String code, long userId, int status, int multiple) {
		Session session = deliveryPlanPendingDelegationDao.getSession();
		String sql = "select * from " + code + "_plan_pending_delegation where f_user_id = :userId";
		sql += " and status = :status " + (multiple > 0 ? "and multiple = :multiple" : "") + " limit 0,1 ";
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("userId", userId);
		query.setInteger("status", status);
		if (multiple > 0) {
			query.setLong("multiple", multiple);
		}
		query.addEntity(DeliveryPlanPendingDelegation.class);
		return (DeliveryPlanPendingDelegation) query.uniqueResult();
	}

}
