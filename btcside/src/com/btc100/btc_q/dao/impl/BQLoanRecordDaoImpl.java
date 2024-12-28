package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQLoanRecordDao;

@Repository("bqLoanRecordDao")
public class BQLoanRecordDaoImpl<T> extends BaseDaoImpl<T> implements BQLoanRecordDao<T> {
	
	
	@Override
	public BigDecimal getSumBorrow(long userId, int borrowId, int state) {
		String stateSql = "";
		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);
		
		if (borrowId > 0) {
			params.put("borrowId", borrowId);
			stateSql += " and borrow_coin_id=:borrowId";
		}
		
		if (state > 0) {
			params.put("state", state);
			stateSql += " and state=:state";
		}
		return sum("select sum(borrow_amount) from BQLoanRecord where f_user_id=:userId " + stateSql, params);
	}

	@Override
	public BigDecimal getSumPledge(long userId, int pledgeId, int state) {
		String stateSql = "";
		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);
		
		if (pledgeId > 0) {
			params.put("pledgeId", pledgeId);
			stateSql += " and pledge_coin_id=:pledgeId";
		}
		
		if (state > 0) {
			params.put("state", state);
			stateSql += " and state=:state";
		}
		return sum("select sum(pledge_amount) from BQLoanRecord where f_user_id=:userId " + stateSql, params);
	}

	@Override
	public BigDecimal getSumBorrowIncome(long borrowUid, int borrowId, int state) {
		String stateSql = "";
		Map<String, Object> params = new HashMap<>();
		params.put("userId", borrowUid);
		
		if (borrowId > 0) {
			params.put("borrowId", borrowId);
			stateSql += " and borrow_coin_id=:borrowId";
		}
		
		if (state > 0) {
			params.put("state", state);
			stateSql += " and state=:state";
		}
		
		if (state == 1) {
			return sum("select sum(interest * (1.0 - fee_rate)) from BQLoanRecord where borrow_uid=:userId " + stateSql, params);
		} else {
			return sum("select sum(interest-fee) from BQLoanRecord where borrow_uid=:userId " + stateSql, params);
		}
	}

	@Override
	public BigDecimal getSumRecived(long userId, int borrowId) {
		String stateSql = "";
		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);
		
		if (borrowId > 0) {
			params.put("borrowId", borrowId);
			stateSql += " and borrow_coin_id=:borrowId";
		}
		
		return sum("select sum(received_amount) from BQLoanRecord where f_user_id=:userId " + stateSql + " and (state = 1 or state = 3)", params);
	}

	@Override
	public BigDecimal getSumInterest(long userId, int borrowId, int state) {
		String stateSql = "";
		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);
		
		if (borrowId > 0) {
			params.put("borrowId", borrowId);
			stateSql += " and borrow_coin_id=:borrowId";
		}
		
		if (state > 0) {
			params.put("state", state);
			stateSql += " and state=:state";
		}
		
		return sum("select sum(interest) from BQLoanRecord where f_user_id=:userId " + stateSql, params);
	}

	@Override
	public BigDecimal getSumBorrowOut(long borrowUid, int borrowId, int state) {
		String stateSql = "";
		Map<String, Object> params = new HashMap<>();
		params.put("userId", borrowUid);
		
		if (borrowId > 0) {
			params.put("borrowId", borrowId);
			stateSql += " and borrow_coin_id=:borrowId";
		}
		
		if (state > 0) {
			params.put("state", state);
			stateSql += " and state=:state";
		}
		
		return sum("select sum(received_amount) from BQLoanRecord where borrow_uid=:userId " + stateSql, params);
	}

	@Override
	public BigDecimal getSumInterestObtain(long borrowUid, int borrowId, int state) {
		String stateSql = "";
		Map<String, Object> params = new HashMap<>();
		params.put("userId", borrowUid);
		
		if (borrowId > 0) {
			params.put("borrowId", borrowId);
			stateSql += " and borrow_coin_id=:borrowId";
		}
		
		if (state > 0) {
			params.put("state", state);
			stateSql += " and state=:state";
		}
		
		return sum("select sum(interest - fee) from BQLoanRecord where borrow_uid=:userId " + stateSql, params);
	}

	@Override
	public BigDecimal getSumPaymentPledgeObtain(long borrowUid, int pledgeId, int state) {
		String stateSql = "";
		Map<String, Object> params = new HashMap<>();
		params.put("userId", borrowUid);
		
		if (pledgeId > 0) {
			params.put("pledgeId", pledgeId);
			stateSql += " and pledge_coin_id=:pledgeId";
		}
		
		if (state > 0) {
			params.put("state", state);
			stateSql += " and state=:state";
		}
		
		return sum("select sum(pledge_amount - fee) from BQLoanRecord where borrow_uid=:userId " + stateSql, params);
	}

	@Override
	public BigDecimal getSumReleasePledge(long userId, int pledgeId, int state) {
		String stateSql = "";
		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);
		
		if (pledgeId > 0) {
			params.put("pledgeId", pledgeId);
			stateSql += " and pledge_coin_id=:pledgeId";
		}
		
		if (state > 0) {
			params.put("state", state);
			stateSql += " and state=:state";
		}
		return sum("select sum(back_pledge_amount) from BQLoanRecord where f_user_id=:userId " + stateSql, params);
	}
	
}
