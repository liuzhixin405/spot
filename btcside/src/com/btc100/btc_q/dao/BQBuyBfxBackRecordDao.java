package com.btc100.btc_q.dao;

import java.math.BigDecimal;

public interface BQBuyBfxBackRecordDao<T> extends BaseDaoI<T> {
	
	public BigDecimal sumBfxAmount(long uid);
	
	public BigDecimal sumBfxAmountFrozen(long uid);

	public BigDecimal sumObtain(long uid, int currencyId);
}
