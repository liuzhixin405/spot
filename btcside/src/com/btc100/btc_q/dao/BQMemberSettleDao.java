package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.BQMemberSettle;

public interface BQMemberSettleDao<T> extends BaseDaoI<T> {
	
	long insertLog(final BQMemberSettle t);
}
