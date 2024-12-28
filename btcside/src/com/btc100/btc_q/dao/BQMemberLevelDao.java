package com.btc100.btc_q.dao;

import java.util.List;

import com.btc100.btc_q.entity.BQMemberLevel;

public interface BQMemberLevelDao<T> extends BaseDaoI<T>{

	BQMemberLevel findByUserId(long fUserId);
	
	public List<T> getAllByCache();
	
	long getLevelByCredits(long value, boolean withHiddenState);
}
