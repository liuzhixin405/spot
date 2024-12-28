package com.btc100.btc_q.dao;

import java.math.BigDecimal;

public interface BQTransferWithOtcRecordDao<T> extends BaseDaoI<T> {
	
	public BigDecimal sumFrozen(long uid, int currencyId, int direction);

}
