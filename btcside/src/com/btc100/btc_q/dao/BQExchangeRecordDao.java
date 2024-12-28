package com.btc100.btc_q.dao;

import java.math.BigDecimal;

public interface BQExchangeRecordDao<T> extends BaseDaoI<T> {
	
	public BigDecimal sumFrozen(long uid, int currencyId);

	public BigDecimal sumDeduction(long uid, int currencyId) ;

	public BigDecimal sumObtain(long uid, int currencyId);
}
