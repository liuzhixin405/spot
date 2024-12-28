package com.btc100.btc_q.dao;

import java.util.List;

public interface BQMemberInviteAmountDao<T> extends BaseDaoI<T> {
	List<T> findByUidAndDay(long uid, String day);
	int addClick(long uid, String day);
	int addSuccess(long uid, String day);
}
