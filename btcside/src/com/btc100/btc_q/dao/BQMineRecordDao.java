package com.btc100.btc_q.dao;

import java.math.BigDecimal;

public interface BQMineRecordDao<T> extends BaseDaoI<T> { 
	
	public BigDecimal sumAmountBetweenTime(long uid, int currencyId, long startTime, long endTime);
}
