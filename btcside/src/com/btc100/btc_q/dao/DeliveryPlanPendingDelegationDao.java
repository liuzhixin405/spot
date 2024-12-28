package com.btc100.btc_q.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.HibernateException;

import com.btc100.btc_q.entity.DeliveryPlanPendingDelegation;
import com.btc100.btc_q.vo.PagerModel;

public interface DeliveryPlanPendingDelegationDao<T> extends BaseDaoI<T> {

	DeliveryPlanPendingDelegation insert(DeliveryPlanPendingDelegation obj) throws HibernateException;

	int deletePending(String code, Long id) throws HibernateException;

	List<DeliveryPlanPendingDelegation> findByTypeAndLastPrice(String code, int type, BigDecimal lastPrice, int limit);
	
	int updateStatusAndNote(String code, Long id, int status, String note);

	List<DeliveryPlanPendingDelegation> findByUserId(String code, long fUserId, int direction, int status);

	List<DeliveryPlanPendingDelegation> findByUserIdDirection(int direction, String code, long fUserId);

	DeliveryPlanPendingDelegation getById(long id, String bqCode, long userId);
	
	PagerModel getPagerModel(String code, Long uid, int direction, int status, int type);
}
