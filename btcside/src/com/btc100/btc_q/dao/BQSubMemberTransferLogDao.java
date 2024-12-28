package com.btc100.btc_q.dao;

import java.math.BigDecimal;

public interface BQSubMemberTransferLogDao<T> extends BaseDaoI<T> {
	
	BigDecimal sumAmount(long userId, int currencyId, boolean isIn);
}
