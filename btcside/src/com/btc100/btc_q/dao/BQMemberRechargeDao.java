package com.btc100.btc_q.dao;

import java.math.BigDecimal;

public interface BQMemberRechargeDao<T> extends BaseDaoI<T> {

	public BigDecimal findSUMByUserID(Long uid, int itype);

}
