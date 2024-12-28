package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.HibernateException;

import com.btc100.btc_q.entity.PlanPendingDelegation;
import com.btc100.btc_q.vo.PagerModel;

public interface PlanPendingDelegationService {

	PlanPendingDelegation insert(PlanPendingDelegation obj) throws HibernateException;

	List<PlanPendingDelegation> findByTypeAndLastPrice(String code, int type, BigDecimal lastPrice, int limit);
	
	String lock(String code, long id);

	void unLock(String code, long id, String value);

	int updateStatusAndNote(String code, Long id, int status, String note);

	int updateStatusAndNoteByMid(String code, Long uid, Long mid, int status, String note);

	int updateWinAndLoss(List<PlanPendingDelegation> planPendingDelegations, String code, Long uid, Long mid);

	int deletePending(String code, Long id) throws HibernateException;

	void deleteLossAmonthAgo(String code, long uid);

	Long getUnix_TIMESTAMP();

	PagerModel getPagerModel(String code, Long uid, int direction, int status, int type);
	
	
	
	///************************************ for read only **************************************/
	
	List<PlanPendingDelegation> readByUserId(String code, long fUserId, int direction, int status);
	
	List<PlanPendingDelegation> readByUserIdDirection(int direction, String code, long fUserId);

	List<PlanPendingDelegation> readWinAndLossByUserIdStatusAndMId(String code, long fUserId, int status, long transactionMemberId);
	
	boolean readHasStopLoss(String code, long fUserId, long transactionMemberId);
	
	PlanPendingDelegation readById(long id, String bqCode, long userId);
	
	List<PlanPendingDelegation> readPendingByUserIdAndTime(String code, long userId, long startTime, long endTime, long from, String direct, int size);
	
	PlanPendingDelegation readOneByStatus(String code, long userId, int status, int multiple);

	PlanPendingDelegation readOneByStatusDirectionAndPositionId(String code, long fUserId, int status, long transactionMemberId, int stoplossId);

}
