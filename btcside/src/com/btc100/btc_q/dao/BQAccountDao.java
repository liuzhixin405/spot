package com.btc100.btc_q.dao;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQMember;

public interface BQAccountDao<T> extends BaseDaoI<T> {
	
	/**
	 * 资金变动使用--如果返回值小于等于0,事务要回滚
	 * @param uid
	 * @param amount
	 * @param frozenAmount
	 * @return
	 */
	int updateAmount(Long uid, int currencyId, BigDecimal amount, BigDecimal frozenAmount);
	
	BQAccount getByUerAndCurrencyId(long uid, int currencyId);
	
	void insertAccount(BQMember member, int currencyId);
}
