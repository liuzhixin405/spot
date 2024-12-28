package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.NMemberTrade;

public interface NMemberTradeDao<T> extends BaseDaoI<T> {

	void insertMemberTrade(String partition, NMemberTrade memberTrade, String code);

}
