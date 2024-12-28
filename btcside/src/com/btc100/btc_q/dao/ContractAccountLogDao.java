package com.btc100.btc_q.dao;

import java.util.List;

public interface ContractAccountLogDao<T> extends BaseDaoI<T> { 
	public com.btc100.btc_q.entity.ContractAccountLog insertLog(final com.btc100.btc_q.entity.ContractAccountLog contractLog);
	
	List<Object[]> findSumZRZCRecord(String bqCode, long userId);
	
	void createAccountLogBak(String code);
	
	void insertAccountLogBak(String code, long endTime);
	
	void deleteAccountLog(String code, long endTime);
}
