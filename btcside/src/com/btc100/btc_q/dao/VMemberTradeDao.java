package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.VMemberTrade;

public interface VMemberTradeDao<T> extends BaseDaoI<T> {

	void insertMemberTrade(VMemberTrade memberTrade, String code);

}
