package com.btc100.btc_q.dao;

import java.math.BigDecimal;

public interface BQIeoAccountDao<T> extends BaseDaoI<T> {
	
	int updateAccount(long uid, int currencyId, BigDecimal initial, BigDecimal locked, BigDecimal unlocked);
}
