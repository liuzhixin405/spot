package com.btc100.btc_q.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.HibernateException;

import com.btc100.btc_q.entity.PlanPendingDelegation;
import com.btc100.btc_q.vo.PagerModel;

public interface PlanPendingDelegationDao<T> extends BaseDaoI<T> {

	PlanPendingDelegation insert(PlanPendingDelegation obj) throws HibernateException;

	int deletePending(String code, Long id) throws HibernateException;

	int deletePendingAmonthAgo(String code, Long uid) throws HibernateException;

	List<PlanPendingDelegation> findByTypeAndLastPrice(String code, int type, BigDecimal lastPrice, int limit);
	
	int updateStatusAndNote(String code, Long id, int status, String note);

	int updateStatusAndNoteByMid(String code, Long uid, Long mid, int status, String note);

	int updateStopWinAndLoss(long id, PlanPendingDelegation pendingDelegation);

	List<PlanPendingDelegation> findByUserId(String code, long fUserId, int direction, int status);

	List<PlanPendingDelegation> findByUserIdDirection(int direction, String code, long fUserId);

	PlanPendingDelegation getById(long id, String bqCode, long userId);

	PlanPendingDelegation getByStatusDirectionAndPositionId(String code, long fUserId, int status, long transactionMemberId, int stoplossId);
	
	PagerModel getPagerModel(String code, Long uid, int direction, int status, int type);
}
