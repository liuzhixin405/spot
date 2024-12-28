package com.btc100.btc_q.dao;

import java.util.List;

import com.btc100.btc_q.entity.DeliveryMemberTradeDetail;

public interface DeliveryMemberTradeDetailDao<T> extends BaseDaoI<T> {

	DeliveryMemberTradeDetail insertTradeDetail(DeliveryMemberTradeDetail tradeDetail);

	public List<Object[]> getAllTopN(String bqCode, int n);

	public List<Object[]> getAllTopN_123(String bqCode, int n);

	public List<Object[]> getAllTopN(String bqCode, int n, long since);
	
	void createMemberTradeDetailBak(String code);
	
	void insertMemberTradeDetailBak(String code, long endTime);
	
	void deleteMemberTradeDetail(String code, long endTime);
}
