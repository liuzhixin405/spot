package com.btc100.btc_q.dao;

import java.util.List;

import com.btc100.btc_q.entity.SpotMemberTradeDetail;

public interface SpotMemberTradeDetailDao<T> extends BaseDaoI<T> {

	SpotMemberTradeDetail insertTradeDetail(SpotMemberTradeDetail tradeDetail);

	public List<Object[]> getAllTopN(String bqCode, int n);

	public List<Object[]> getAllTopN_123(String bqCode, int n);

	public List<Object[]> getAllTopN(String bqCode, int n, long since);
	
	void createMemberTradeDetailBak(String code);
	
	void insertMemberTradeDetailBak(String code, long endTime);
	
	void deleteMemberTradeDetail(String code, long endTime);
}
