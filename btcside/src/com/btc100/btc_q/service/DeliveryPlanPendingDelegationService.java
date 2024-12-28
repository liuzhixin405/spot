package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.HibernateException;

import com.btc100.btc_q.entity.DeliveryPlanPendingDelegation;
import com.btc100.btc_q.vo.PagerModel;

public interface DeliveryPlanPendingDelegationService {

	DeliveryPlanPendingDelegation insert(DeliveryPlanPendingDelegation obj) throws HibernateException;

	List<DeliveryPlanPendingDelegation> findByTypeAndLastPrice(String code, int type, BigDecimal lastPrice, int limit);
	
	String lock(String code, long id);

	void unLock(String code, long id, String value);

	int updateStatusAndNote(String code, Long id, int status, String note);

	int deletePending(String code, Long id) throws HibernateException;
	
	Long getUnix_TIMESTAMP();

	PagerModel getPagerModel(String code, Long uid, int direction, int status, int type);
	
	
	
	///************************************ for read only **************************************/
	
	List<DeliveryPlanPendingDelegation> readByUserId(String code, long fUserId, int direction, int status);
	
	List<DeliveryPlanPendingDelegation> readByUserIdDirection(int direction, String code, long fUserId);
	
	DeliveryPlanPendingDelegation readById(long id, String bqCode, long userId);
	
	List<DeliveryPlanPendingDelegation> readPendingByUserIdAndTime(String code, long userId, long startTime, long endTime, long from, String direct, int size);
	
	DeliveryPlanPendingDelegation readOneByStatus(String code, long userId, int status, int multiple);
}
