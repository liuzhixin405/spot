package com.btc100.btc_q.dao;

import java.math.BigDecimal;

public interface BQIeoUnlockRecordDao<T> extends BaseDaoI<T> { 
	
	public BigDecimal sumAmountBetweenTime(long uid, long startTime, long endTime);
	
}
