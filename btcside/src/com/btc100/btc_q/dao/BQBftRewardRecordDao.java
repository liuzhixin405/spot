package com.btc100.btc_q.dao;

import java.math.BigDecimal;

public interface BQBftRewardRecordDao<T> extends BaseDaoI<T> {
	
	public BigDecimal sumAmountBetweenTime(long uid, int currencyId, long startTime, long endTime);
}
