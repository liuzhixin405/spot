package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.HibernateException;

import com.btc100.btc_q.entity.NPlanPendingDelegation;
import com.btc100.btc_q.vo.PagerModel;

public interface NPlanPendingDelegationService {

	NPlanPendingDelegation insert(String partition, NPlanPendingDelegation obj) throws HibernateException;

	List<NPlanPendingDelegation> findByTypeAndLastPrice(String partition, String code, int type, BigDecimal lastPrice, int limit);

	String lock(String partition, String code, long id);

	void unLock(String partition, String code, long id, String value);

	int updateStatusAndNote(String partition, String code, Long id, int status, String note);

	int updateStatusAndNoteByMid(String partition, String code, Long uid, Long mid, int status, String note);

	int updateWinAndLoss(String partition, List<NPlanPendingDelegation> nPlanPendingDelegations, String code, Long uid, Long mid);

	int deletePending(String partition, String code, Long id) throws HibernateException;

	void deleteLossAmonthAgo(String partition, String code, long uid);

	Long getUnix_TIMESTAMP(String partition);

	PagerModel getPagerModel(String partition, String code, Long uid, int direction, int status, int type);
	
	
	
	///************************************ for read only **************************************/
	
	List<NPlanPendingDelegation> readByUserId(String partition, String code, long fUserId, int direction, int status);

	List<NPlanPendingDelegation> readByUserIdDirection(String partition, int direction, String code, long fUserId);

	List<NPlanPendingDelegation> readWinAndLossByUserIdStatusAndMId(String partition, String code, long fUserId, int status, long transactionMemberId);
	
	boolean readHasStopLoss(String partition, String code, long fUserId, long transactionMemberId);

	NPlanPendingDelegation readById(String partition, long id, String bqCode, long userId);
	
	List<NPlanPendingDelegation> readPendingByUserIdAndTime(String partition, String code, long userId, long startTime, long endTime, long from, String direct, int size);

	NPlanPendingDelegation readOneByStatus(String partition, String code, long userId, int status, int multiple);

	NPlanPendingDelegation readOneByStatusDirectionAndPositionId(String partition, String code, long fUserId, int status, long transactionMemberId, int stoplossId);

}
