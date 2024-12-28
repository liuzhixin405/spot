package com.btc100.btc_q.dao;

import java.util.List;

public interface VContractAccountLogDao<T> extends BaseDaoI<T> { 
	public com.btc100.btc_q.entity.VContractAccountLog insertLog(final com.btc100.btc_q.entity.VContractAccountLog contractLog);
	
	List<Object[]> findSumZRZCRecord(String vCode, long userId);
	
	void createAccountLogBak(String code);
	
	void insertAccountLogBak(String code, long endTime);
	
	void deleteAccountLog(String code, long endTime);
}
