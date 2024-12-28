package com.btc100.btc_q.dao;

import java.math.BigDecimal;

public interface BQExchangeBfcRecordDao<T> extends BaseDaoI<T> {

	BigDecimal sumFrozen(long uid, int currencyId);

	BigDecimal sumDeduction(long uid, int currencyId);

	BigDecimal sumObtain(long uid, int currencyId);
}
