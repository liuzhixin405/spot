package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.NPlanPendingDelegationDao;
import com.btc100.btc_q.dao.NStopLossDao;
import com.btc100.btc_q.entity.NPlanPendingDelegation;
import com.btc100.btc_q.entity.NStopLoss;
import com.btc100.btc_q.service.NPlanPendingDelegationService;
import com.btc100.btc_q.vo.PagerModel;

@Service("nPlanPendingDelegationService")
public class NPlanPendingDelegationServiceImpl implements NPlanPendingDelegationService {

	@Autowired
	private NPlanPendingDelegationDao<NPlanPendingDelegation> planPendingDelegationDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private NStopLossDao<NStopLoss> stopLossDao;

	@Override
	public NPlanPendingDelegation insert(String partition, NPlanPendingDelegation obj) throws HibernateException {
		NPlanPendingDelegation pd = planPendingDelegationDao.insert(partition, obj);
		return pd;
	}

	@Override
	public List<NPlanPendingDelegation> findByTypeAndLastPrice(String partition, String code, int type, BigDecimal lastPrice, int limit) {
		return planPendingDelegationDao.findByTypeAndLastPrice(partition, code, type, lastPrice, limit);
	}

	@Override
	public String lock(String partition, String code, long id) {
		return spyMemcachedClient.lock(BTCQMemecacheKeys.PLAN_PENDING_LOCK + partition + "_" + code + "_" + id);
	}

	@Override
	public void unLock(String partition, String code, long id, String value) {
		spyMemcachedClient.unlock(BTCQMemecacheKeys.PLAN_PENDING_LOCK + partition + "_" + code + "_" + id, value);
	}

	@Override
	public int updateStatusAndNote(String partition, String code, Long id, int status, String note) {
		return planPendingDelegationDao.updateStatusAndNote(partition, code, id, status, note);
	}

	@Override
	public int updateStatusAndNoteByMid(String partition, String code, Long uid, Long mid, int status, String note) {
		return planPendingDelegationDao.updateStatusAndNoteByMid(partition, code, uid, mid, status, note);
	}

	@Override
	public int updateWinAndLoss(String partition, List<NPlanPendingDelegation> nPlanPendingDelegations, String code, Long uid, Long mid) {
		planPendingDelegationDao.updateStatusAndNoteByMid(partition, code, uid, mid, Contents.PLAN_PENDING_STATUS_CANCEL, "止盈止损撤销");

		if (nPlanPendingDelegations.size() > 0) {
			deleStopLoss(partition, nPlanPendingDelegations.get(0).getTransaction_member_id());
		}
		for (NPlanPendingDelegation delegation : nPlanPendingDelegations) {
			NPlanPendingDelegation bqt = planPendingDelegationDao.getByStatusDirectionAndPositionId(partition, delegation.getF_code(), delegation.getF_user_id(), -1, delegation.getTransaction_member_id(), delegation.getStop_loss_type_id());
			if (null != bqt) {
				planPendingDelegationDao.updateStopWinAndLoss(partition, bqt.getId(), delegation);
			} else {
				planPendingDelegationDao.insert(partition, delegation);
			}
		}
		return 1;
	}

	private void deleStopLoss(String partition, long mid) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("mid", mid);
		stopLossDao.deleteConditionBySql("delete from " + partition + "_stop_loss where transaction_member_id = :mid", params);
	}

	@Override
	public int deletePending(String partition, String code, Long id) throws HibernateException {
		return planPendingDelegationDao.deletePending(partition, code, id);
	}

	@Override
	public void deleteLossAmonthAgo(String partition, String code, long uid) {
		planPendingDelegationDao.deletePendingAmonthAgo(partition, code, uid);
	}

	@Override
	public List<NPlanPendingDelegation> readByUserId(String partition, String code, long fUserId, int direction, int status) {
		return planPendingDelegationDao.findByUserId(partition, code, fUserId, direction, status);
	}

	@Override
	public List<NPlanPendingDelegation> readByUserIdDirection(String partition, int direction, String code, long fUserId) {
		return planPendingDelegationDao.findByUserIdDirection(partition, direction, code, fUserId);
	}

	@Override
	public List<NPlanPendingDelegation> readWinAndLossByUserIdStatusAndMId(String partition, String code, long fUserId, int status, long transactionMemberId) {
		Session session = planPendingDelegationDao.getSession();
		String sql = "select * from " + partition + "_" + code + "_plan_pending_delegation where f_user_id = :userId and status = :status and stop_loss_type_id > 0 ";
		if (transactionMemberId > 0) {
			sql += " and transaction_member_id = :transactionMemberId";
		}
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("userId", fUserId);
		query.setInteger("status", status);
		query.setLong("transactionMemberId", transactionMemberId);
		query.addEntity(NPlanPendingDelegation.class);
		return query.list();
	}

	public boolean readHasStopLoss(String partition, String code, long fUserId, long transactionMemberId) {
		Session session = planPendingDelegationDao.getSession();
		String sql = "select count(1) from " + partition + "_" + code + "_plan_pending_delegation where f_user_id = :userId and status = 0 and transaction_member_id = :transactionMemberId";
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("userId", fUserId);
		query.setLong("transactionMemberId", transactionMemberId);
		return ((BigInteger) query.uniqueResult()).intValue() > 0;
	}
	
	@Override
	public NPlanPendingDelegation readById(String partition, long id, String bqCode, long userId) {
		return planPendingDelegationDao.getById(partition, id, bqCode, userId);
	}

	@Override
	public Long getUnix_TIMESTAMP(String partition) {
		return planPendingDelegationDao.getUnix_TIMESTAMP();
	}

	@Override
	public PagerModel getPagerModel(String partition, String code, Long uid, int direction, int status, int type) {
		return planPendingDelegationDao.getPagerModel(partition, code, uid, direction, status, type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NPlanPendingDelegation> readPendingByUserIdAndTime(String partition, String code, long userId, long startTime,
			long endTime, long from, String direct, int size) {
		Session session = planPendingDelegationDao.getSession();
		String sql = "select * from " + partition + "_" + code + "_plan_pending_delegation where f_user_id = :userId";
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
		
		sql += " order by case when status=0 then 0 else 1 end asc, delegation_time desc ";
		
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
		
		query.addEntity(NPlanPendingDelegation.class);
		
		return query.list();
	}

	@Override
	public NPlanPendingDelegation readOneByStatus(String partition, String code, long userId, int status, int multiple) {
		Session session = planPendingDelegationDao.getSession();
		String sql = "select * from " + partition + "_" + code + "_plan_pending_delegation where f_user_id = :userId";
		sql += " and status = :status " + (multiple > 0 ? "and multiple = :multiple" : "") + " limit 0,1 ";
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("userId", userId);
		query.setInteger("status", status);
		if (multiple > 0) {
			query.setParameter("multiple", multiple);
		}
		query.addEntity(NPlanPendingDelegation.class);
		return (NPlanPendingDelegation) query.uniqueResult();
	}

	@Override
	public NPlanPendingDelegation readOneByStatusDirectionAndPositionId(String partition, String code, long fUserId, int status, long transactionMemberId, int stoplossId) {
		return planPendingDelegationDao.getByStatusDirectionAndPositionId(partition, code, fUserId, status, transactionMemberId, stoplossId);
	}

}
