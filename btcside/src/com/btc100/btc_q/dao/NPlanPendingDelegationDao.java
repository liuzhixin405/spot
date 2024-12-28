package com.btc100.btc_q.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.HibernateException;

import com.btc100.btc_q.entity.NPlanPendingDelegation;
import com.btc100.btc_q.vo.PagerModel;

public interface NPlanPendingDelegationDao<T> extends BaseDaoI<T> {

	NPlanPendingDelegation insert(String partition, NPlanPendingDelegation obj) throws HibernateException;

	int deletePending(String partition, String code, Long id) throws HibernateException;

	int deletePendingAmonthAgo(String partition, String code, Long uid) throws HibernateException;

	List<NPlanPendingDelegation> findByTypeAndLastPrice(String partition, String code, int type, BigDecimal lastPrice, int limit);
	
	int updateStatusAndNote(String partition, String code, Long id, int status, String note);

	int updateStatusAndNoteByMid(String partition, String code, Long uid, Long mid, int status, String note);

	int updateStopWinAndLoss(String partition, long id, NPlanPendingDelegation nPendingDelegation);

	List<NPlanPendingDelegation> findByUserId(String partition, String code, long fUserId, int direction, int status);

	List<NPlanPendingDelegation> findByUserIdDirection(String partition, int direction, String code, long fUserId);

	NPlanPendingDelegation getById(String partition, long id, String bqCode, long userId);

	NPlanPendingDelegation getByStatusDirectionAndPositionId(String partition, String code, long fUserId, int status, long transactionMemberId, int stoplossId);

	PagerModel getPagerModel(String partition, String code, Long uid, int direction, int status, int type);
}
