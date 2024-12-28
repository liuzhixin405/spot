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
import com.btc100.btc_q.dao.BQStopLossDao;
import com.btc100.btc_q.dao.PlanPendingDelegationDao;
import com.btc100.btc_q.entity.BQStopLoss;
import com.btc100.btc_q.entity.PlanPendingDelegation;
import com.btc100.btc_q.service.PlanPendingDelegationService;
import com.btc100.btc_q.vo.PagerModel;

@Service("planPendingDelegationService")
public class PlanPendingDelegationServiceImpl implements PlanPendingDelegationService {

	@Autowired
	private PlanPendingDelegationDao<PlanPendingDelegation> planPendingDelegationDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQStopLossDao<BQStopLoss> bqStopLossDao;

	@Override
	public PlanPendingDelegation insert(PlanPendingDelegation obj) throws HibernateException {
		PlanPendingDelegation pd = planPendingDelegationDao.insert(obj);
		return pd;
	}

	@Override
	public List<PlanPendingDelegation> findByTypeAndLastPrice(String code, int type, BigDecimal lastPrice, int limit) {
		return planPendingDelegationDao.findByTypeAndLastPrice(code, type, lastPrice, limit);
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
		return planPendingDelegationDao.updateStatusAndNote(code, id, status, note);
	}

	@Override
	public int updateStatusAndNoteByMid(String code, Long uid, Long mid, int status, String note) {
		return planPendingDelegationDao.updateStatusAndNoteByMid(code, uid, mid, status, note);
	}

	@Override
	public int updateWinAndLoss(List<PlanPendingDelegation> planPendingDelegations, String code, Long uid, Long mid) {
		planPendingDelegationDao.updateStatusAndNoteByMid(code, uid, mid, Contents.PLAN_PENDING_STATUS_CANCEL, "止盈止损撤销");

		if (planPendingDelegations.size() > 0) {
			deleteStopLoss(planPendingDelegations.get(0).getTransaction_member_id());
		}
		for (PlanPendingDelegation delegation : planPendingDelegations) {
			PlanPendingDelegation bqt = planPendingDelegationDao.getByStatusDirectionAndPositionId(delegation.getF_code(), delegation.getF_user_id(), -1, delegation.getTransaction_member_id(), delegation.getStop_loss_type_id());
			if (null != bqt) {
				planPendingDelegationDao.updateStopWinAndLoss(bqt.getId(), delegation);
			} else {
				planPendingDelegationDao.insert(delegation);
			}
		}
		return 1;
	}

	private void deleteStopLoss(long mid) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("mid", mid);
		bqStopLossDao.deleteCondition("delete from BQStopLoss where transaction_member_id = :mid", params);
	}

	@Override
	public int deletePending(String code, Long id) throws HibernateException {
		return planPendingDelegationDao.deletePending(code, id);
	}

	@Override
	public void deleteLossAmonthAgo(String code, long uid) {
		planPendingDelegationDao.deletePendingAmonthAgo(code, uid);
	}

	@Override
	public List<PlanPendingDelegation> readByUserId(String code, long fUserId, int direction, int status) {
		return planPendingDelegationDao.findByUserId(code, fUserId, direction, status);
	}

	@Override
	public List<PlanPendingDelegation> readByUserIdDirection(int direction, String code, long fUserId) {
		return planPendingDelegationDao.findByUserIdDirection(direction, code, fUserId);
	}

	@Override
	public List<PlanPendingDelegation> readWinAndLossByUserIdStatusAndMId(String code, long fUserId, int status, long transactionMemberId) {
		Session session = planPendingDelegationDao.getSession();
		String sql = "select * from " + code + "_plan_pending_delegation where f_user_id = :userId and status = :status and stop_loss_type_id > 0 ";
		if (transactionMemberId > 0) {
			sql += " and transaction_member_id = :transactionMemberId";
		}
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("userId", fUserId);
		query.setInteger("status", status);
		query.setLong("transactionMemberId", transactionMemberId);
		query.addEntity(PlanPendingDelegation.class);
		return query.list();
	}
	
	@Override
	public boolean readHasStopLoss(String code, long fUserId, long transactionMemberId) {
		Session session = planPendingDelegationDao.getSession();
		String sql = "select count(1) from " + code + "_plan_pending_delegation where f_user_id = :userId and status = 0 and transaction_member_id = :transactionMemberId";
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("userId", fUserId);
		query.setLong("transactionMemberId", transactionMemberId);
		return ((BigInteger) query.uniqueResult()).intValue() > 0;
	}

	@Override
	public PlanPendingDelegation readById(long id, String bqCode, long userId) {
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
	public List<PlanPendingDelegation> readPendingByUserIdAndTime(String code, long userId, long startTime,
			long endTime, long from, String direct, int size) {
		Session session = planPendingDelegationDao.getSession();
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
		
		query.addEntity(PlanPendingDelegation.class);
		
		return query.list();
	}

	@Override
	public PlanPendingDelegation readOneByStatus(String code, long userId, int status, int multiple) {
		Session session = planPendingDelegationDao.getSession();
		String sql = "select * from " + code + "_plan_pending_delegation where f_user_id = :userId";
		sql += " and status = :status " + (multiple > 0 ? "and multiple = :multiple" : "") + " limit 0,1 ";
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("userId", userId);
		query.setInteger("status", status);
		if (multiple > 0) {
			query.setLong("multiple", multiple);
		}
		query.addEntity(PlanPendingDelegation.class);
		return (PlanPendingDelegation) query.uniqueResult();
	}

	@Override
	public PlanPendingDelegation readOneByStatusDirectionAndPositionId(String code, long fUserId, int status, long transactionMemberId, int stoplossId) {
		return planPendingDelegationDao.getByStatusDirectionAndPositionId(code, fUserId, status, transactionMemberId, stoplossId);
	}

}
