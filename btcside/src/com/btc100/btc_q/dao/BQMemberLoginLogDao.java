package com.btc100.btc_q.dao;

public interface BQMemberLoginLogDao<T> extends BaseDaoI<T> {

	Long getTotalLoginUserByTime(Long startTime, Long endTime);
}
