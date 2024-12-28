package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.SpotMemberTrade;

public interface SpotMemberTradeDao<T> extends BaseDaoI<T> {

	void insertSpotMemberTrade(SpotMemberTrade memberTrade, String code);

}
