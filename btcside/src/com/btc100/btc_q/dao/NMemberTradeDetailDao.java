package com.btc100.btc_q.dao;

import java.util.List;

import com.btc100.btc_q.entity.NMemberTradeDetail;

public interface NMemberTradeDetailDao<T> extends BaseDaoI<T> {

	NMemberTradeDetail insertTradeDetail(String partition, NMemberTradeDetail tradeDetail);

	public List<Object[]> getAllTopN(String partition, String vCode, int n);

	public List<Object[]> getAllTopN_123(String partition, String vCode, int n);

	public List<Object[]> getAllTopN(String partition, String vCode, int n, long since);

	void createMemberTradeDetailBak(String partition, String code);
	
	void insertMemberTradeDetailBak(String partition, String code, long endTime);
	
	void deleteMemberTradeDetail(String partition, String code, long endTime);
}
