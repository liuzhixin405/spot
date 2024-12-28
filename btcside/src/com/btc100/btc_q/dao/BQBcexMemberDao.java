package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.BQBcexMember;

public interface BQBcexMemberDao<T> extends BaseDaoI<T>{
	
	BQBcexMember getByEmail(String email);
}
