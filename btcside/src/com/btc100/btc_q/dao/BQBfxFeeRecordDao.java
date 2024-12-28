package com.btc100.btc_q.dao;

import java.math.BigDecimal;

public interface BQBfxFeeRecordDao<T> extends BaseDaoI<T> {
	
	BigDecimal sumAmount(long userId);
}
