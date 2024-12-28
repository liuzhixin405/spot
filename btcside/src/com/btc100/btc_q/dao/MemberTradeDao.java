package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.MemberTrade;

public interface MemberTradeDao<T> extends BaseDaoI<T> {

	void insertMemberTrade(MemberTrade memberTrade, String code);

}
