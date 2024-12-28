package com.btc100.btc_q.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.HibernateException;

import com.btc100.btc_q.entity.VPlanPendingDelegation;
import com.btc100.btc_q.vo.PagerModel;

public interface VPlanPendingDelegationDao<T> extends BaseDaoI<T> {

	VPlanPendingDelegation insert(VPlanPendingDelegation obj) throws HibernateException;

	int deletePending(String code, Long id) throws HibernateException;

	List<VPlanPendingDelegation> findByTypeAndLastPrice(String code, int type, BigDecimal lastPrice, int limit);
	
	int updateStatusAndNote(String code, Long id, int status, String note);

	List<VPlanPendingDelegation> findByUserId(String code, long fUserId, int direction, int status);

	List<VPlanPendingDelegation> findByUserIdDirection(int direction, String code, long fUserId);

	VPlanPendingDelegation getById(long id, String bqCode, long userId);
	
	PagerModel getPagerModel(String code, Long uid, int direction, int status, int type);
}
