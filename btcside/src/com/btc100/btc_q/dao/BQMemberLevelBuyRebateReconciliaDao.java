package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.BQMemberLevelBuyRebateReconcilia;

public interface BQMemberLevelBuyRebateReconciliaDao<T> extends BaseDaoI<T> {
	
	int updateRecord(T record);
	
	BQMemberLevelBuyRebateReconcilia getByUerId(long uid);
	
}
