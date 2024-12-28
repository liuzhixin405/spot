package com.btc100.btc_q.dao;

import java.math.BigDecimal;

public interface BQMemberLevelBuyAmountLogDao<T> extends BaseDaoI<T>{

	BigDecimal sumAmount(long userId, int currencyId);
	
	int updateRecord(long id, BigDecimal locked, BigDecimal unlocked);
	
}
