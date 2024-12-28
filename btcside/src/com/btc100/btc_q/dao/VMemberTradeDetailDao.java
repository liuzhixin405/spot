package com.btc100.btc_q.dao;

import java.util.List;

import com.btc100.btc_q.entity.VMemberTradeDetail;

public interface VMemberTradeDetailDao<T> extends BaseDaoI<T> {

	VMemberTradeDetail insertTradeDetail(VMemberTradeDetail tradeDetail);

	public List<Object[]> getAllTopN(String vCode, int n);

	public List<Object[]> getAllTopN_123(String vCode, int n);

	public List<Object[]> getAllTopN(String vCode, int n, long since);

	void createMemberTradeDetailBak(String code);
	
	void insertMemberTradeDetailBak(String code, long endTime);
	
	void deleteMemberTradeDetail(String code, long endTime);
}
